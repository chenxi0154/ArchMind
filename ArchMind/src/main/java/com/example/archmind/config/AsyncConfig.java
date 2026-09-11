package com.example.archmind.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("analysisExecutor")
    public ThreadPoolTaskExecutor analysisExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setCorePoolSize(4);
            executor.setMaxPoolSize(8);
            executor.setQueueCapacity(100);
            executor.setThreadNamePrefix("analysis-");
            executor.setRejectedExecutionHandler(
                    new ThreadPoolExecutor.CallerRunsPolicy());
            executor.initialize();
            return executor;
    }

}
