package com.OMS.contest.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class minioConfig {

    @Bean
    public MinioClient minioClient() {
        // 替换为你的MinIO服务器地址、访问密钥和秘密密钥
        String endpoint = "https://your-minio-server.com";
        String accessKey = "your-access-key";
        String secretKey = "your-secret-key";

        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
