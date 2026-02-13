package com.pathz.dbconcurrency.repository;

import com.pathz.dbconcurrency.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountRepository {

    // УВАГА: Тут немає FOR UPDATE. Це дозволяє Race Condition.
    @Select("SELECT * FROM account WHERE id = #{id}")
    Account findById(Integer id);

    @Update("UPDATE account SET balance = #{balance} WHERE id = #{id}")
    void update(Account account);
    
    // Для скидання балансу перед тестом
    @Update("UPDATE account SET balance = 1000 WHERE id = #{id}")
    void resetBalance(Integer id);
}