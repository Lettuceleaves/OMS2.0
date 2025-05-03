package com.OMS.run.controller;

import com.OMS.run.service.runService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
public class testCtrller {

    @Autowired
    runService runService;

    @GetMapping({"/hello"})
    public String hello() {
        return "Hello World!";
    }

    @PostMapping("/testMinioTransfer")
    public String testMinioTransfer(@RequestParam("file") byte[] file) {
        return new String(file) +
                " successfully received!";
    }

    @PostMapping("/testFilesTrasfer")
    public String testFilesTrasfer(@RequestParam("file") byte[][] files) {
        System.out.println(files.length);
        System.out.println(files[0].length);
        StringBuilder sb = new StringBuilder();
        for (byte[] file : files) {
            sb.append(new String(file)).append("\n");
        }
        return sb.toString();
    }

}
