package com.OMS.contest.service.impl;

import com.OMS.contest.client.runClient;
import com.OMS.contest.repos.minioRepos;
import com.OMS.contest.repos.mybatisRepos;
import com.OMS.contest.service.contestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class contestServiceImpl implements contestService {

    @Autowired
    private runClient runClient;

    @Autowired
    private minioRepos minioRepos;

    @Autowired
    private mybatisRepos mybatisRepos;

    public String submit(String problemName, MultipartFile userFile) {
        // 使用mybatisRepos select所有匹配
        return null;
    }
}
