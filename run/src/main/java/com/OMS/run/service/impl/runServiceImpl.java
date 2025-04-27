package com.OMS.run.service.impl;

import com.OMS.run.client.runClient;
import com.OMS.run.service.runService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class runServiceImpl implements runService {

    @Autowired
    private runClient runClient;

    @Autowired
    private cacheServiceImpl cacheService;

    @Override
    public String[] run(MultipartFile userFile, String problemName, String language) throws Exception {
        MultipartFile[] inputFiles = cacheService.getInputFile(problemName);
        return new String[]{"a", "b"};
    }
}
