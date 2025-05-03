package com.OMS.contest.service.impl;

import com.OMS.contest.repos.mybatisRepos;
import com.OMS.contest.service.contestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class contestServiceImpl implements contestService {

    @Autowired
    private mybatisRepos mybatisRepos;

    public String submit(String problemName, byte[] userFile) {
        return null;
    }
}
