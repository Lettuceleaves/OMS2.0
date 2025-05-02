package org.example.advice.controller;

import org.example.advice.service.adviceService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
public class controllerApplication {

    @Autowired
    private adviceService adviceService;

    @GetMapping("/advice")
    public Flux<ServerSentEvent<String>> advice(@RequestParam(value = "userFile") String userFile) throws IOException {
        return adviceService.advice(userFile);
    }
}