package com.OMS.contest.repos;

import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface minioRepos {
    MultipartFile downloadFileAsMultipartFile(String bucketName, String objectName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException;
}
