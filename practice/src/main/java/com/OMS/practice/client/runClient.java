package com.OMS.practice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "run", url = "localhost:1111")
@RestController

public interface runClient {

    @PostMapping("/runFeign")
    List<byte[]> testFeign(@RequestBody List<byte[]> files) throws Exception;

}
