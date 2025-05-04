package com.OMS.contest.controller;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class testCtrller {

    @Autowired
    private MinioClient minioClient;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Contest Service!";
    }
}
