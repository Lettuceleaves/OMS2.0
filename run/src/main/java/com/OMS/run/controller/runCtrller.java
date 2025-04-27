package com.OMS.run.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class runCtrller {

    @GetMapping("/test")
    public String test() {
        return "Hello, World!";
    }
}
