package com.OMS.practice.controller;

import com.OMS.practice.client.adviceClient;
import com.OMS.practice.model.problem;
import com.OMS.practice.service.practiceService;
import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
public class practiceCtrller {

    @Autowired
    private practiceService practiceService;

    @Autowired
    private adviceClient client;

    @GetMapping("testAdvice")
    public Flux<ServerSentEvent<String>> getAdvice(@RequestParam("userFile") MultipartFile userFile) throws IOException {
        return client.advice(new String(userFile.getBytes()));
    }

    @GetMapping("list/{page}")
    public List<problem> getProblemList(@PathVariable int page) {
        return practiceService.getProblemList(page);
    }

    @PostMapping("/submit/{problemName}")
    public String submit(@PathVariable("problemName") String problemName, @RequestParam("userFile") MultipartFile userFile) throws Exception {
        return practiceService.submit(problemName, userFile.getBytes());
    }
}
