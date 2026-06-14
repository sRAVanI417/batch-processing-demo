package com.example.batch_processing_demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import java.io.File;


@Configuration
@RequiredArgsConstructor
public class SftpConfig {

    private final SftpProperties properties;

    @Bean
    public DefaultSftpSessionFactory sftpSessionFactory() {

        DefaultSftpSessionFactory factory =
                new DefaultSftpSessionFactory(true);

        factory.setHost(properties.getHost());
        factory.setPort(properties.getPort());
        factory.setUser(properties.getUsername());
        factory.setPassword(properties.getPassword());

        factory.setAllowUnknownKeys(true);

        return factory;
    }

    @Bean
    public SftpInboundFileSynchronizer synchronizer(
            DefaultSftpSessionFactory factory) {

        SftpInboundFileSynchronizer synchronizer =
                new SftpInboundFileSynchronizer(factory);

        synchronizer.setRemoteDirectory(
                properties.getRemoteDirectory());

        synchronizer.setDeleteRemoteFiles(false);

        synchronizer.setPreserveTimestamp(true);

        synchronizer.setFilter(
                new SftpSimplePatternFileListFilter("*.csv"));

        return synchronizer;
    }
//
//    @Bean
//    public CommandLineRunner testSftp(
//            DefaultSftpSessionFactory factory) {
//
//        return args -> {
//            try (var session = factory.getSession()) {
//                System.out.println("SFTP Connected Successfully");
//                var entries = session.list("upload");
//
//                for (var entry : entries) {
//                    System.out.println(entry.getFilename());
//                }
//            }
//        };
//    }

    @Bean
    public SftpInboundFileSynchronizingMessageSource sftpMessageSource(
            SftpInboundFileSynchronizer synchronizer) {

        SftpInboundFileSynchronizingMessageSource source =
                new SftpInboundFileSynchronizingMessageSource(
                        synchronizer);

        source.setLocalDirectory(
                new File(properties.getLocalDirectory()));

        source.setAutoCreateLocalDirectory(true);

        source.setLocalFilter(
                new AcceptOnceFileListFilter<>());

        return source;
    }
}