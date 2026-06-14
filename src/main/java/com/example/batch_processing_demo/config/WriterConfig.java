package com.example.batch_processing_demo.config;

import com.example.batch_processing_demo.dto.SalesRecord;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class WriterConfig {

    @Bean
    public JdbcBatchItemWriter<SalesRecord> inventoryWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<SalesRecord>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO sales(
                        id,
                        itemname,
                        quantitysold,
                        price,
                        remainingstock
                    )
                    VALUES(
                        :id,
                        :itemName,
                        :quantitySold,
                        :price,
                        :remainingStock
                    )
                    ON CONFLICT(id)
                    DO UPDATE SET
                        itemname = EXCLUDED.itemname,
                        quantitysold = EXCLUDED.quantitysold,
                        price = EXCLUDED.price,
                        remainingstock = EXCLUDED.remainingstock,
                        updated_at = CURRENT_TIMESTAMP
                    """)
                .beanMapped()
                .build();
    }
}