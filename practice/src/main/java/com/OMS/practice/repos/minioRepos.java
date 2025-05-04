package com.OMS.practice.repos;

import io.minio.errors.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface minioRepos {
    byte[] downloadFile(String bucketName, String objectName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;
    String newDirectory(String bucketName, String directoryName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;
    String deleteFile(String bucketName, String objectName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;
    String uploadFile(String bucketName, String objectName, byte[] data) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;
    String deleteDirectory(String bucketName, String directoryName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;
}
