package com.OMS.run.service;

import org.springframework.web.multipart.MultipartFile;

public interface cacheService {
    MultipartFile[] getInputFile(String problemName);
}
