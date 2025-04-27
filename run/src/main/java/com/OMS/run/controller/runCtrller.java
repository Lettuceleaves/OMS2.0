package com.OMS.run.controller;

import com.OMS.run.service.k8sService;
import io.kubernetes.client.openapi.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class runCtrller {

    @Autowired
    private k8sService k8s;

    @GetMapping("/test")
    public String test() throws ApiException, IOException {
        k8s.test();
        return "test success";
    }

    @GetMapping("/jobTest")
    public String jobTest() throws ApiException, IOException {
        k8s.jobTest();
        return "jobTest success";
    }
}
