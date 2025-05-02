package com.OMS.practice.service;

import com.OMS.practice.model.problem;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

public interface practiceService {
    String submit(String problemName, MultipartFile userFile);
    Flux<ServerSentEvent<String>> advice(MultipartFile userFile) throws IOException;
    List<problem> getProblemList(int page);
}
