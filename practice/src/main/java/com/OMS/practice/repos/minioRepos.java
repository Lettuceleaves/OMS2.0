package com.OMS.practice.repos;

import io.minio.errors.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface minioRepos {
    byte[] downloadFile(String bucketName, String objectName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;
}
