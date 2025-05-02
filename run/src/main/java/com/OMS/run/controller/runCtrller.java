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

    @PostMapping("/run")
    public String[] test(@RequestParam("input") MultipartFile[] files, @RequestParam("user") MultipartFile u) throws Exception {
        return runService.run(u,"c", files);
    }

}
