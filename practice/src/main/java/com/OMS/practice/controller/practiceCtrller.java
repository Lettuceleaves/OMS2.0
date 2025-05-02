package com.OMS.practice.controller;

import com.OMS.practice.model.problem;
import com.OMS.practice.service.practiceService;
import feign.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
public class practiceCtrller {

    @Autowired
    private practiceService practiceService;

    @GetMapping("advice")
    public Flux<ServerSentEvent<String>> getAdvice(@RequestParam("userFile") MultipartFile userFile) throws IOException {
        return practiceService.advice(userFile);
    }

    @GetMapping("list/{page}")
    public List<problem> getProblemList(@PathVariable int page) {
        return practiceService.getProblemList(page);
    }
}
