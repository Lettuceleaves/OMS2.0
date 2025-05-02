package com.OMS.run.controller;

import com.OMS.run.service.runService;
import io.kubernetes.client.openapi.ApiException;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class runCtrller {

    @Autowired
    runService runService;

    @PostMapping("/testFile")
    public String test(@RequestParam("file1") MultipartFile f1, @RequestParam("file2") MultipartFile f2, @RequestParam("file3") MultipartFile f3, @RequestParam("user") MultipartFile u) throws Exception {
        MultipartFile[] inputFiles = new MultipartFile[3];
        inputFiles[0] = f1;
        inputFiles[1] = f2;
        inputFiles[2] = f3;
        System.out.println("test");
        String[] ans;
        ans = runService.run(u, "hello", "c", inputFiles);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ans.length; i++) {
            sb.append(ans[i] + "\n");
        }
        return sb.toString();
    }

    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        return "Hello World!";
    }

}
