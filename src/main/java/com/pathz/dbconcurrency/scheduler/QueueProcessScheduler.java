package com.pathz.dbconcurrency.scheduler;

import com.pathz.dbconcurrency.entity.QueueTask;
import com.pathz.dbconcurrency.repository.QueueTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oleksandr Klymenko
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueueProcessScheduler {

    private final QueueTaskRepository queueTaskRepository;

    @Scheduled(fixedDelay = 1000L)
    // @Async тут ще можна таке накинути. Тобто паралелізація буде не
    //  тільки в розрізі нод, але і в розрізі потоків
    @Transactional
    public void process() {
        QueueTask queueTask = queueTaskRepository.selectForProcess();
        if (queueTask != null) {
            log.info("Processing queue task {}", queueTask);
            queueTaskRepository.updateStatus(queueTask.getId(), "processed");
        } else {
            log.info("No queue task found");
        }
    }
}
