package com.OMS.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class runApplication {

    public static void main(String[] args) {
        SpringApplication.run(runApplication.class, args);
        System.out.println("runApplication starts successfully!");
    }

}
