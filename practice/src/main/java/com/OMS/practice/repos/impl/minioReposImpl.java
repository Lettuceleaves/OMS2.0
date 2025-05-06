package com.OMS.practice.repos.impl;

import com.OMS.practice.repos.minioRepos;
import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;


@Repository
public class minioReposImpl implements minioRepos {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean checkExistFile(String bucketName, String objectName) {
        try {
            // 检查文件是否存在
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return true; // 文件存在
        } catch (MinioException e) {
            if (e.getMessage().contains("NoSuchKey")) {
                return false; // 文件不存在
            }
            e.printStackTrace();
            return false; // 发生其他错误
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false; // 发生其他错误
        }
    }

    // 给目标文件夹改名
    public boolean changeDirName(String bucketName, String oldDirectoryName, String newDirectoryName) {
        try {
            // 检查旧目录是否存在
            if (!checkExistFile(bucketName, oldDirectoryName)) {
                return false; // 旧目录不存在
            }

            // 列出旧目录下的所有对象
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(oldDirectoryName)
                            .recursive(true)
                            .build()
            );

            // 遍历所有对象并重命名
            for (Result<Item> result : results) {
                Item item = result.get();
                String oldObjectName = item.objectName();
                String newObjectName = newDirectoryName + oldObjectName.substring(oldDirectoryName.length());

                // 复制对象到新位置
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucketName)
                                .object(newObjectName)
                                .source(CopySource.builder()
                                        .bucket(bucketName)
                                        .object(oldObjectName)
                                        .build())
                                .build()
                );

                // 删除旧对象
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(oldObjectName)
                                .build()
                );
            }

            return true; // 重命名成功
        } catch (MinioException e) {
            e.printStackTrace();
            return false; // 发生错误
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false; // 发生其他错误
        }
    }

    public byte[] downloadFile(String bucketName, String objectName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {

        // 检查Redis中是否存在文件内容
        String redisKey = bucketName + ":" + objectName;
        String fileContent = redisTemplate.opsForValue().get(redisKey);
        if (fileContent != null) {
            System.out.println(fileContent + " from redis");
            return fileContent.getBytes();
        }

        System.out.println("Downloading file from MinIO: " + bucketName + "/" + objectName);

        // 从MinIO下载文件，获取InputStream
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build())) {

            // 将文件保存到redis

            byte[] data = inputStream.readAllBytes();
            redisTemplate.opsForValue().set(redisKey, new String(data));

            // 将InputStream转换为byte数组
            return data;
        }
    }

    public String uploadFile(String bucketName, String objectName, byte[] data) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // 使用MinIO客户端上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .contentType("application/octet-stream")
                            .build()
            );

            // 将文件元数据存储到 Redis
            String redisKey = bucketName + ":" + objectName;
            redisTemplate.opsForValue().set(redisKey, new String(data));

            return "File uploaded successfully";

        } catch (MinioException e) {
            e.printStackTrace();
            throw new IOException("Error occurred while uploading file to MinIO: " + e.getMessage(), e);
        }
    }

    public String deleteFile(String bucketName, String objectName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // 使用MinIO客户端删除文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            // 删除 Redis 中的缓存
            String redisKey = bucketName + ":" + objectName;
            redisTemplate.delete(redisKey);

            return "File deleted successfully";
        } catch (MinioException e) {
            e.printStackTrace();
            throw new IOException("Error occurred while deleting file from MinIO: " + e.getMessage(), e);
        }
    }

    public String newDirectory(String bucketName, String directoryName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // MinIO does not have a direct API to create directories,
            // but you can create a zero-byte object with a trailing slash to simulate a directory.
            String objectName = directoryName.endsWith("/") ? directoryName : directoryName + "/";
            byte[] data = new byte[0]; // Zero-byte data for the "directory"

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(new ByteArrayInputStream(data), data.length, -1)
                            .contentType("application/octet-stream")
                            .build()
            );
            return "Directory created successfully";
        } catch (MinioException e) {
            e.printStackTrace();
            throw new IOException("Error occurred while creating directory in MinIO: " + e.getMessage(), e);
        }
    }

    public String deleteDirectory(String bucketName, String directoryName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        try {
            // MinIO does not have a direct API to delete directories,
            // but you can delete all objects with the specified prefix (directory name).
            String objectName = directoryName.endsWith("/") ? directoryName : directoryName + "/";

            // 先删除redis缓存中的内容，
            String redisKey = bucketName + ":" + directoryName;
            // 根据前缀匹配所有满足的key
            Set<String> keys = redisTemplate.keys(redisKey + "*");
            if (keys != null) {
                for (String key : keys) {
                    redisTemplate.delete(key);
                }
            }

            // List and delete all objects with the specified prefix
            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(objectName)
                            .recursive(true)
                            .build()
            );

            for (Result<Item> result : results) {
                Item item = result.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(item.objectName())
                                .build()
                );
            }
            return "Directory deleted successfully";
        } catch (MinioException e) {
            e.printStackTrace();
            throw new IOException("Error occurred while deleting directory in MinIO: " + e.getMessage(), e);
        }
    }

}
