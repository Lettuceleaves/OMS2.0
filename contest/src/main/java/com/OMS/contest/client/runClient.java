package com.OMS.contest.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "run")
@RestController
public interface runClient {
    @PostMapping("/run")
    String[] test(@RequestParam("input") MultipartFile[] files, @RequestParam("user") MultipartFile u) throws Exception;
}
