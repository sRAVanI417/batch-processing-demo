package com.example.batch_processing_demo.config;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {

    @Bean
    public Job inventoryJob(
            JobRepository jobRepository,
            Step inventoryStep) {

        return new JobBuilder(
                "inventoryJob",
                jobRepository)

                .start(inventoryStep)

                .build();
    }
}