package com.pathz.dbconcurrency.service;

import com.pathz.dbconcurrency.entity.Account;
import com.pathz.dbconcurrency.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oleksandr Klymenko
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Цей метод вразливий до Race Condition (Lost Update).
     * @Transactional забезпечує атомарність змін в БД (commit/rollback),
     * АЛЕ НЕ ізоляцію даних від читання іншими транзакціями під час роботи (в READ COMMITTED).
     */
    @Transactional
    public void transfer(Integer fromId, Integer toId, Long amount) {
        // 1. READ (Без блокування)
        Account from = accountRepository.findById(fromId);
        Account to = accountRepository.findById(toId);

        if (from.getBalance() < amount) {
            throw new RuntimeException("Недостатньо коштів");
        }

        // 2. MODIFY (Імітація затримки, щоб інший потік встиг вклинитися і прочитати старі дані)
        try {
            Thread.sleep(500); // 100 мс затримки для наочності race condition
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        // 3. WRITE (Один потік перезапише зміни іншого)
        accountRepository.update(from);
        accountRepository.update(to);

        log.info("Transferred {} from {} to {}. New Balances: {} / {}",
                amount, fromId, toId, from.getBalance(), to.getBalance());
    }

    public void reset() {
        accountRepository.resetBalance(1);
        accountRepository.resetBalance(2);
    }

    public Account getAccount(Integer id) {
        return accountRepository.findById(id);
    }
}