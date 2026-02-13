package com.pathz.dbconcurrency.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueTask {
    private Integer id;
    private String payload;
    private String status;
}