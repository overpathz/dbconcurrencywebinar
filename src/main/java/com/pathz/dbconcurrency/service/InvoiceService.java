package com.pathz.dbconcurrency.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class InvoiceService {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    public InvoiceService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Transactional
    public String generateInvoiceNumber(Long companyId) {
        // Отримуємо advisory lock
        String lockSql = "SELECT pg_advisory_xact_lock(:namespace, :companyId)";
        jdbcTemplate.queryForObject(lockSql, 
            Map.of("namespace", 1001, "companyId", companyId),
            Void.class
        );
        
        // Генеруємо номер (тепер безпечно)
        String sql = """
            SELECT COALESCE(MAX(invoice_number), 0) + 1 
            FROM invoices 
            WHERE company_id = :companyId
            """;
        
        Integer nextNumber = jdbcTemplate.queryForObject(sql, 
            Map.of("companyId", companyId), 
            Integer.class
        );
        
        return String.format("INV-%d-%05d", companyId, nextNumber);
        
        // Lock автоматично звільниться після @Transactional
    }
    
    // Non-blocking варіант
    public Optional<String> tryGenerateInvoiceNumber(Long companyId) {
        String trySql = "SELECT pg_try_advisory_lock(:namespace, :companyId)";
        Boolean acquired = jdbcTemplate.queryForObject(trySql, 
            Map.of("namespace", 1001, "companyId", companyId), 
            Boolean.class
        );
        
        if (Boolean.FALSE.equals(acquired)) {
            return Optional.empty(); // Lock зайнятий
        }
        
        try {
            String sql = """
                SELECT COALESCE(MAX(invoice_number), 0) + 1 
                FROM invoices 
                WHERE company_id = :companyId
                """;
            
            Integer nextNumber = jdbcTemplate.queryForObject(sql, 
                Map.of("companyId", companyId), 
                Integer.class
            );
            
            return Optional.of(String.format("INV-%d-%05d", companyId, nextNumber));
            
        } finally {
            // Звільняємо lock
            String unlockSql = "SELECT pg_advisory_unlock(:namespace, :companyId)";
            jdbcTemplate.queryForObject(unlockSql, 
                Map.of("namespace", 1001, "companyId", companyId), 
                Boolean.class
            );
        }
    }
}