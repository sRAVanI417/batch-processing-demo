package com.example.batch_processing_demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sftp")
@Getter
@Setter
public class SftpProperties {

    private String host;
    private Integer port;
    private String username;
    private String password;

    private String remoteDirectory;
    private String localDirectory;
}