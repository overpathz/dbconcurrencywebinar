package com.pathz.dbconcurrency.controller;

import com.pathz.dbconcurrency.entity.Account;
import com.pathz.dbconcurrency.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Запускає тест на Race Condition.
     * Ми спробуємо зробити 100 переказів по 10 одиниць.
     * Очікувано: з рахунку 1 має піти 1000 (100 * 10).
     * Реально (через баг): піде набагато менше.
     */
    @GetMapping("/race-demo")
    public String startRaceConditionDemo() throws InterruptedException {
        accountService.reset();
        long transferAmount = 10L;
        accountService.transfer(1, 2, transferAmount);

        return "ready";
    }
    
    @GetMapping("/1")
    public Account getAlice() {
        return accountService.getAccount(1);
    }
}