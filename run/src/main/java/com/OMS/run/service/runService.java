package com.OMS.run.service;

import org.springframework.web.multipart.MultipartFile;

public interface runService {
     // String[] run(MultipartFile userFile, String problemName, String language) throws Exception;

     String[] run(MultipartFile userFile, String language, MultipartFile[] inputFiles) throws Exception;
}
