package com.OMS.contest.repos.impl;

import com.OMS.contest.repos.minioRepos;
import com.OMS.contest.tool.CustomMultipartFile;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Repository
public class minioReposImpl implements minioRepos {

    @Autowired
    private MinioClient minioClient;

    public MultipartFile downloadFileAsMultipartFile(String bucketName, String objectName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        // 从MinIO下载文件，获取InputStream
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build())) {

            // 将InputStream转换为byte数组
            byte[] bytes = inputStream.readAllBytes();

            // 创建一个CustomMultipartFile
            return new CustomMultipartFile("file", objectName, "text/plain", bytes);
        }
    }

}
