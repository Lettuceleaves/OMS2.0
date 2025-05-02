package com.OMS.contest.controller;

import com.OMS.contest.service.contestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class contestCtrller {

    @Autowired
    private contestService service; // Assuming you have a service interface for contest operations

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Contest Service!";
    }

    @GetMapping("/contest/getInfo/{contestTitle}")
    public String getContestInfo(@PathVariable("contestTitle") String contestTitle) { // contest class TODO
        // Logic to create a contest with the given name
        return "Contest " + contestTitle + " created successfully!";
    }

    @PostMapping("/contest/submit/{problemName}")
    public String submit(@PathVariable("problemName") String problemName, @RequestParam("userFile") MultipartFile userFile) throws Exception {
        return service.submit(problemName, userFile);
    }
}
