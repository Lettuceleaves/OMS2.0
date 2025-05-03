package com.OMS.run.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface runService {
     // String[] run(MultipartFile userFile, String problemName, String language) throws Exception;

     List<byte[]> run(byte[] userFile, String language, List<byte[]> inputFiles) throws Exception;
}
