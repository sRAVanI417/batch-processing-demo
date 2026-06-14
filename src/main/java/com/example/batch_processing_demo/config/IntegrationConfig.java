package com.example.batch_processing_demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class IntegrationConfig {

    private final JobOperator jobOperator;

    private final Job inventoryJob;

    @Bean
    public IntegrationFlow sftpInboundFlow(
            SftpInboundFileSynchronizingMessageSource source) {

        return IntegrationFlow
                .from(
                        source,
                        c -> c.poller(
                                Pollers.fixedDelay(15000)))

                .handle(message -> {

                    File file = (File) message.getPayload();

                    System.out.println("Downloaded file: "
                            + file.getAbsolutePath());

                    try {

                        JobParameters parameters = new JobParametersBuilder()
                                .addString(
                                        "inputFile",
                                        file.getAbsolutePath())
                                .addLong(
                                        "run.id",
                                        System.currentTimeMillis())
                                .toJobParameters();


                        JobExecution jobExecution =
                                jobOperator.start(
                                        inventoryJob,
                                        parameters);

                        System.out.println(
                                "Started inventoryJob. Execution Id = "
                                        + jobExecution.getId());

                    } catch (Exception e) {
                        throw new RuntimeException(
                                "Failed to launch batch job",
                                e);
                    }
                })

                .get();
    }
}