package com.OMS.run.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "fileManage")
@RestController
public interface runClient {

    @GetMapping("/fileManage/{problemName}")
    MultipartFile[] getInputFiles(@PathVariable("problemName") String problemName);
}
