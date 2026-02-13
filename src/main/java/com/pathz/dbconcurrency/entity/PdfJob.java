package com.pathz.dbconcurrency.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdfJob {
    private Integer id;
    private String status;
    private LocalDateTime leaseUntil;
}