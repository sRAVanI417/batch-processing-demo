package com.example.batch_processing_demo.processor;

import com.example.batch_processing_demo.dto.SalesRecord;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class InventoryProcessor
        implements ItemProcessor<SalesRecord, SalesRecord> {

    @Override
    public SalesRecord process(SalesRecord item) {

        if (item.getId() == null) {
            throw new ValidationException("sNo cannot be null");
        }

        if (item.getQuantitySold() < 0) {
            throw new ValidationException("Negative quantity");
        }

        item.setItemName(item.getItemName().trim());

        return item;
    }
}