package org.example.advice.service.impl;

import org.example.advice.service.adviceService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import javax.swing.plaf.multi.MultiButtonUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class adviceServiceImpl implements adviceService {

    @Autowired
    private ChatClient chatClient;

    public Flux<ServerSentEvent<String>> advice(String userFile) throws IOException {
        return chatClient.prompt()
                .user(userFile)
                .stream()
                .content()
                .map(token -> {
                    return ServerSentEvent.<String>builder().data(token).build();
                });
    }
}
