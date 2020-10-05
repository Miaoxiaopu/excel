package com.fileinfo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "minio-config")
public class MinioProps {
    private String endpoint;

    private int port;

    private String accessKey;

    private String secretKey;

    private String bucketName;
}
