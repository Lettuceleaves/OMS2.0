package com.OMS.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import static java.lang.Thread.sleep;

@SpringBootApplication
@EnableFeignClients
public class runApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(runApplication.class, args);
        System.out.println("runApplication starts successfully!");
    }
}
