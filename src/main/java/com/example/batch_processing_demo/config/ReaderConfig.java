package com.example.batch_processing_demo.config;

import com.example.batch_processing_demo.dto.SalesRecord;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.LineMapper;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class ReaderConfig {

    @Bean
    @StepScope
    public FlatFileItemReader<SalesRecord> inventoryReader(
            @Value("#{jobParameters['inputFile']}") String inputFile) {

        FlatFileItemReader<SalesRecord> reader =
                new FlatFileItemReader<>(new FileSystemResource(inputFile), lineMapper());

        return reader;
    }

    @Bean
    public LineMapper<SalesRecord> lineMapper() {

        DefaultLineMapper<SalesRecord> mapper =
                new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer =
                new DelimitedLineTokenizer();

        tokenizer.setDelimiter(",");

        tokenizer.setNames(
                "sNo",
                "itemName",
                "quantitySold",
                "price",
                "remainingStock");

        BeanWrapperFieldSetMapper<SalesRecord> fieldMapper =
                new BeanWrapperFieldSetMapper<>();

        fieldMapper.setTargetType(SalesRecord.class);

        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(fieldMapper);

        return mapper;
    }
}