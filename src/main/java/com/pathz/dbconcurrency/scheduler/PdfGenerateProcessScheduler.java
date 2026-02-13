package com.pathz.dbconcurrency.scheduler;

import com.pathz.dbconcurrency.entity.PdfJob;
import com.pathz.dbconcurrency.repository.PdfJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Oleksandr Klymenko
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGenerateProcessScheduler {

    private final PdfJobRepository pdfJobRepository;
    private final RestTemplate restTemplate;

    @Scheduled(fixedDelay = 1000L)
    public void process() {
        // 1. Короткий запит в БД: знайти і "орендувати" на 5 хвилин (300 сек)
        // Це відповідає блоку Single Session у твоєму прикладі
        PdfJob job = pdfJobRepository.acquireLease(300);

        if (job == null) {
            return; // Немає задач
        }

        try {
            // 2. Довга обробка (генерація PDF) - 2 хвилини
            // Це відбувається БЕЗ відкритої транзакції в БД
            generatePdf(job);

            // 3. Оновлення статусу
            pdfJobRepository.completeJob(job.getId());
        } catch (Exception e) {
            // Якщо впали - нічого страшного.
            // Через 5 хвилин lease_until закінчиться, і задачу зможе підхопити інший процес
            // (за умови, що ми скинемо статус в pending або змінимо SQL відбору)
            log.error("Failed to process job " + job.getId(), e);
        }
    }

    private void generatePdf(PdfJob job) {
        log.info("Починаємо генерацію PDF для задачі {}...", job.getId());

        // ІМІТАЦІЯ: Викликаємо зовнішній сервіс, який "тупить" 5 секунд.
        // Це емулює процес генерації файлу.
        String simulationUrl = "https://httpbin.org/delay/5";

        // Можна також передати ID, щоб імітувати контекст
        // String simulationUrl = "https://jsonplaceholder.typicode.com/posts/" + job.getId();

        try {
            // Робимо GET запит
            String response = restTemplate.getForObject(simulationUrl, String.class);

            // Тут можна додати логіку перевірки відповіді
            log.debug("Отримано відповідь від зовнішнього API: {}", response != null ? "OK" : "Empty");

        } catch (Exception e) {
            throw new RuntimeException("Зовнішній сервіс генерації PDF недоступний", e);
        }

        log.info("Генерація PDF для задачі {} завершена (імітація).", job.getId());
    }
}
