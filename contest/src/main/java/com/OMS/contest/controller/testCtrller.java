package com.OMS.contest.controller;

import com.OMS.contest.client.runClient;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
public class testCtrller {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private runClient runClient;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Contest Service!";
    }

    @GetMapping("/minioTest")
    public String minioTest(@RequestParam(value = "bn") String bucketName,@RequestParam(value = "on") String objectName) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build())) {

            // 将InputStream转换为byte数组
            byte[] bytes = inputStream.readAllBytes();
            return runClient.testMinioTransfer(bytes);
        } catch (MinioException e) {
            e.printStackTrace();
            return "Error occurred while accessing MinIO: " + e.getMessage();
        }
    }
}
