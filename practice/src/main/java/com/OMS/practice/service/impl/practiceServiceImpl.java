package com.OMS.practice.service.impl;

import com.OMS.practice.client.runClient;
import com.OMS.practice.model.problem;
import com.OMS.practice.repos.minioRepos;
import com.OMS.practice.repos.mybatisRepos;
import com.OMS.practice.service.practiceService;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class practiceServiceImpl implements practiceService {

    @Autowired
    private mybatisRepos mybatisRepos;

    @Autowired
    private minioRepos minioRepos;

    @Autowired
    private runClient runClient;

    @Override
    public List<problem> getProblemList(int page) {
        return mybatisRepos.getProblems(page);
    }

    public String submit(String problemName, byte[] userFile) throws Exception {
        problem problemInfo = mybatisRepos.getProblemByName(problemName);
        if (problemInfo == null) throw new RuntimeException("Problem not found");
        int caseNum = problemInfo.getCaseNum();
        List<byte[]> cases = new java.util.ArrayList<>();
        for (int i = 0; i < caseNum; i++) {
            byte[] caseFile = minioRepos.downloadFile("case", problemName + "/" + i + ".txt");
            cases.add(caseFile);
        }
        List<byte[]> result = runClient.testFeign(cases, userFile); // TODO

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < caseNum; i++) {
            System.out.println(new String(cases.get(i)));
            sb.append(new String(result.get(i)));
        }
        return sb.toString();
    }

}
