package com.pathz.dbconcurrency.repository;

import com.pathz.dbconcurrency.entity.QueueTask;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface QueueTaskRepository {

    // Мапінг простий, бо назви полів збігаються, але пропишемо явно для надійності
    @Results(id = "queueMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "payload", column = "payload"),
            @Result(property = "status", column = "status")
    })
    @Select("SELECT * FROM queue WHERE id = #{id}")
    QueueTask findById(Integer id);

    /**
     * Знаходить задачу зі статусом 'new', блокує її та переводить у 'processing'.
     * Логіка dntu (часу) прибрана, бо поля немає в таблиці.
     */
    @ResultMap("queueMap")
    @Select("""
        WITH next_task AS (
            SELECT id
            FROM queue
            WHERE status = 'new'
            ORDER BY id ASC
            LIMIT 1
            FOR UPDATE SKIP LOCKED
        )
        UPDATE queue q
        SET status = 'processing'
        FROM next_task
        WHERE q.id = next_task.id
        RETURNING q.*
    """)
    QueueTask selectForProcess();

    @Insert("INSERT INTO queue (payload, status) VALUES (#{payload}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(QueueTask task);

    @Update("UPDATE queue SET status = #{status} WHERE id = #{id}")
    void updateStatus(@Param("id") Integer id, @Param("status") String status);
}