package com.fileinfo.config;

import com.fileinfo.utils.MinioUtils;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.thymeleaf.expression.Numbers;

import java.util.Arrays;
import java.util.Locale;

@Slf4j
@Configuration
public class FileConfig {

    @Autowired
    private MinioProps minioProps;

    @Bean
    public CommonsMultipartResolver getCommonsMultipartResolver(){
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(50*1024*1024);
        return commonsMultipartResolver;
    }
    @Bean
    public MinioUtils getMinioUtils(){
        MinioUtils minioUtils = new MinioUtils();
        return minioUtils;
    }
    @Bean
    public MinioClient getMinioClient() throws InvalidPortException, InvalidEndpointException {
        log.info("[------->minio配置" + minioProps.toString() + "]");
        return new MinioClient(minioProps.getEndpoint(),minioProps.getPort(),minioProps.getAccessKey(),minioProps.getSecretKey());
    }
}
