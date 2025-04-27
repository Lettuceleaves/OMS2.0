package com.OMS.run.service;

import org.springframework.web.multipart.MultipartFile;

public interface k8sService {
    String[] runJob(MultipartFile userFile, MultipartFile[] input, int timelimitms, String language) throws Exception;
}
