package com.pathz.dbconcurrency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author Oleksandr Klymenko
 */
@Configuration
public class GeneralStuffConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
