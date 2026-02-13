package com.pathz.dbconcurrency.entity;

import lombok.Data;

@Data
public class Account {
    private Integer id;
    private String name;
    private Long balance;
}