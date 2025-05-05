package com.OMS.advice.service;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

public interface adviceService {
    Flux<ServerSentEvent<String>> advice(String userFile) throws IOException;
}
