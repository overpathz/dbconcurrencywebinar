package com.example.concurrency.exception;

import com.pathz.dbconcurrency.exception.OptimisticLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<?> handleOptimisticLock(OptimisticLockException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                        "success", false,
                        "error", "OPTIMISTIC_LOCK_CONFLICT",
                        "message", e.getMessage()
                )
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(
                Map.of(
                        "success", false,
                        "error", "BAD_REQUEST",
                        "message", e.getMessage()
                )
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataAccess(DataAccessException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Map.of(
                        "success", false,
                        "error", "DATABASE_ERROR",
                        "message", "Database operation failed: " + e.getMessage()
                )
        );
    }
}