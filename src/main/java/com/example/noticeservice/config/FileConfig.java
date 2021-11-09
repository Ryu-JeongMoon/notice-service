package com.example.noticeservice.config;

import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
@MultipartConfig
public class FileConfig {

    @Value("${spring.servlet.multipart.file-size-threshold}")
    private long fileSizeThreshold;
    @Value("${spring.servlet.multipart.max-file-size}")
    private long maxFileSize;
    @Value("${spring.servlet.multipart.max-request-size}")
    private long maxRequestSize;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();

        factory.setFileSizeThreshold(DataSize.ofMegabytes(fileSizeThreshold));
        factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize));
        factory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize));

        return factory.createMultipartConfig();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
