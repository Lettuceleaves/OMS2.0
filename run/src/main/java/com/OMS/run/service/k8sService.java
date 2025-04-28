package com.OMS.run.service;

import io.kubernetes.client.openapi.ApiException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface k8sService {
    String[] runJob(MultipartFile userFile, MultipartFile[] input, int timelimitms, String language) throws Exception;
    void test() throws ApiException, IOException;
    void jobTest () throws ApiException, IOException;
    void jobFileTest () throws ApiException, IOException;
}
