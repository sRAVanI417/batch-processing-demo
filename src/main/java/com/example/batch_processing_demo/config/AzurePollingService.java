package com.example.batch_processing_demo.config;

import com.example.batch_processing_demo.azure.AzureBlobDownloader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AzurePollingService {

    private final AzureBlobDownloader azureBlobDownloader;

    private final JobOperator jobOperator;

    @Scheduled(
            fixedDelayString = "${azure.polling.interval:60000}")
    public void pollAzureStorage() {

        try {

            log.info("Polling Azure Blob Storage...");

            List<File> files =
                    azureBlobDownloader.downloadFiles();

            if (files.isEmpty()) {

                log.info("No files found.");

                return;
            }

            for (File file : files) {

                launchJob(file);
            }

        } catch (Exception ex) {

            log.error(
                    "Error while polling Azure Blob Storage",
                    ex);
        }
    }

    private void launchJob(File file) {

        try {

            String parameters =
                    "inputFile="
                            + file.getAbsolutePath()
                            + ",run.id="
                            + System.currentTimeMillis();

            Long executionId =
                    jobOperator.start(
                            "inventoryJob",
                            parameters);

            log.info(
                    "Started inventoryJob. Execution Id={}",
                    executionId);

        } catch (Exception ex) {

            log.error(
                    "Failed to start job for file {}",
                    file.getName(),
                    ex);
        }
    }
}
