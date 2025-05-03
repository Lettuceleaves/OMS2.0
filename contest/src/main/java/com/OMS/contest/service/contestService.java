package com.OMS.contest.service;

import org.springframework.web.multipart.MultipartFile;

public interface contestService {
    String submit(String problemName, byte[] userFile);
}
