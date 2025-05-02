package com.OMS.practice.service.impl;

import com.OMS.practice.client.runClient;
import com.OMS.practice.model.problem;
import com.OMS.practice.repos.reposInterface;
import com.OMS.practice.service.practiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class practiceServiceImpl implements practiceService {

    @Autowired
    private reposInterface repository;

    @Autowired
    private runClient client;

    @Override
    public String submit(String problemName, MultipartFile userFile) {
        return "hello world";
    }

    @Override
    public Flux<ServerSentEvent<String>> advice(MultipartFile userFile) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(userFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        }
        return client.advice(fileContent.toString());
    }

    @Override
    public List<problem> getProblemList(int page) {
        return repository.getProblems(page);
    }
}
