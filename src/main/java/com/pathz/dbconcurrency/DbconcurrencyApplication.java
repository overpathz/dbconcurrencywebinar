package com.pathz.dbconcurrency;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.pathz.dbconcurrency.repository")
@EnableScheduling
public class DbconcurrencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbconcurrencyApplication.class, args);
	}

}
