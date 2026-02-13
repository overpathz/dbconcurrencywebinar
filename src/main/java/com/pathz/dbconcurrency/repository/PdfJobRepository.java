package com.pathz.dbconcurrency.repository;

import com.pathz.dbconcurrency.entity.PdfJob;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PdfJobRepository {

    @Results(id = "jobMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "status", column = "status"),
        @Result(property = "leaseUntil", column = "lease_until")
    })
    @Select("SELECT * FROM pdf_jobs WHERE id = #{id}")
    PdfJob findById(Integer id);

    /**
     * Атомарно знаходить задачу, блокує її та встановлює час оренди.
     * Реалізує патерн "Do Not Take Until" через lease_until.
     *
     * @param leaseSeconds кількість секунд, на які орендується задача (час на обробку)
     * @return оновлений об'єкт задачі або null, якщо задач немає
     */
    @ResultMap("jobMap")
    @Select("""
        WITH task AS (
            SELECT id
            FROM pdf_jobs
            WHERE status = 'pending'
              AND (lease_until IS NULL OR lease_until < now())
            ORDER BY id
            LIMIT 1
            FOR UPDATE SKIP LOCKED
        )
        UPDATE pdf_jobs j
        SET
            lease_until = now() + (#{leaseSeconds} || ' second')::interval,
            status = 'processing'
        FROM task
        WHERE j.id = task.id
        RETURNING j.id, j.status, j.lease_until
    """)
    PdfJob acquireLease(@Param("leaseSeconds") int leaseSeconds);

    /**
     * Завершує виконання задачі.
     */
    @Update("UPDATE pdf_jobs SET status = 'done' WHERE id = #{id}")
    void completeJob(@Param("id") Integer id);

    // Допоміжний метод для створення тестових даних
    @Insert("INSERT INTO pdf_jobs (status) VALUES ('pending')")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void createJob(PdfJob job);
}