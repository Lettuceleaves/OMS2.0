package com.OMS.run.controller;

import com.OMS.run.service.runService;
import io.kubernetes.client.openapi.ApiException;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class runCtrller {

    @Autowired
    runService runService;

    @PostMapping("/runFeign")
    public List<byte[]> testFeign(@RequestBody List<byte[]> files) throws Exception {
        return runService.run("c", files);
    }
}
