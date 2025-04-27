package com.OMS.run.service.impl;

import com.OMS.run.client.runClient;
import com.OMS.run.service.cacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class cacheServiceImpl implements cacheService {

    @Autowired
    @Lazy
    private runClient runClient;

    @Cacheable(value = "inputFiles", key = "#problemName")
    public MultipartFile[] getInputFile(String problemName) {
        return runClient.getInputFiles(problemName);
    }
}
