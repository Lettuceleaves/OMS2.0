package com.OMS.run.service.impl;

import com.OMS.run.client.runClient;
import com.OMS.run.service.runService;
import okio.Path;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Random;
import java.util.UUID;

@Service
public class runServiceImpl implements runService {

    @Autowired
    private runClient runClient;

    @Autowired
    private cacheServiceImpl cacheService;

    @Override
    public String[] run(MultipartFile userFile, String problemName, String language) throws Exception {
        MultipartFile[] inputFiles = cacheService.getInputFile(problemName);
        // 生成uuid加一串随机字符加时间戳
        String dirName = System.currentTimeMillis() + UUID.randomUUID().toString() + RandomStringUtils.randomAlphabetic(10);
        // 在当前目录的父目录的父目录的父目录的localRun文件夹下创建一个文件夹，文件夹名为problemName
        String path = System.getProperty("user.dir") + "/../../localRun/" + problemName + "/" + dirName;
        // 创建文件夹
        Path.get(path).toFile().mkdirs();
        // 将userFile保存到path目录下
        File userFilePath = new File(path + "/target." + language);
        userFile.transferTo(userFilePath);
        // 将inputFiles保存到path目录下
        if (inputFiles != null) {
            for (int i = 0; i < inputFiles.length; i++) {
                File inputFilePath = new File(path + "/input" + (i + 1) + ".txt");
                inputFiles[i].transferTo(inputFilePath);
            }
        } else throw new Exception("No input files found for problem: " + problemName);
        // 在该目录下新建out.txt文件
        File outputFilePath = new File(path + "/out.txt");
        if (!outputFilePath.createNewFile()) {
            throw new Exception("Failed to create output file at: " + outputFilePath.getAbsolutePath());
        }
        // 将父目录下的monitor子目录中的run可执行文件复制到path目录下
        String runPath = System.getProperty("user.dir") + "/../../monitor/run";
        Files.copy(Paths.get(path), Paths.get(runPath), StandardCopyOption.REPLACE_EXISTING);
        // 执行run可执行文件
        ProcessBuilder processBuilder = new ProcessBuilder(runPath, userFilePath.getAbsolutePath(), outputFilePath.getAbsolutePath());
        processBuilder.directory(new File(path));
        Process process = processBuilder.start();
        // 等待执行完成, 或者超时
        boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
        if (!finished) {
            process.destroy();
            throw new Exception("Execution timed out after 10 seconds.");
        }
        return new String[] { runPath, outputFilePath.getAbsolutePath() };
    }
}
