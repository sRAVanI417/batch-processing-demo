package com.example.batch_processing_demo.listener;

import com.example.batch_processing_demo.dto.SalesRecord;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class InventorySkipListener
        implements SkipListener<SalesRecord, SalesRecord> {

    @Override
    public void onSkipInProcess(
            SalesRecord item,
            Throwable t) {

        System.err.println(
                "Skipped item: "
                        + item
                        + ", reason="
                        + t.getMessage());
    }
}