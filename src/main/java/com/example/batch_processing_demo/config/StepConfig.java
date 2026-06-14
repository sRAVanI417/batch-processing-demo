package com.example.batch_processing_demo.config;

import com.example.batch_processing_demo.dto.SalesRecord;
import com.example.batch_processing_demo.listener.InventorySkipListener;
import com.example.batch_processing_demo.processor.InventoryProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileParseException;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class StepConfig {

    private final InventorySkipListener skipListener;

    @Bean
    public Step inventoryStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<SalesRecord> inventoryReader,
            InventoryProcessor processor,
            JdbcBatchItemWriter<SalesRecord> inventoryWriter) {

        return new StepBuilder(
                "inventoryStep",
                jobRepository)

                .<SalesRecord, SalesRecord>chunk(1000)
                .transactionManager(transactionManager)

                .reader(inventoryReader)

                .processor(processor)

                .writer(inventoryWriter)

                /*
                 * Skip Logic
                 */
                .faultTolerant()

                .skip(ValidationException.class)

                .skip(FlatFileParseException.class)

                .skipLimit(1000)

                /*
                 * Retry Logic
                 */
                .retry(DeadlockLoserDataAccessException.class)

                .retry(CannotAcquireLockException.class)

                .retryLimit(3)

                .listener(skipListener)

                .build();
    }
}