package com.OMS.run.service.impl;

import com.OMS.run.service.runService;
import okio.Path;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class runServiceImpl implements runService {

    @Override
    public String[] run(MultipartFile userFile, String language, MultipartFile[] inputFiles) throws Exception {
//        MultipartFile[] inputFiles = cacheService.testGetInputFile(problemName); TODO
        // 生成uuid加一串随机字符加时间戳
        String dirName = System.currentTimeMillis() + UUID.randomUUID().toString() + RandomStringUtils.randomAlphabetic(10);
        // 在当前目录的父目录的父目录的父目录的localRun文件夹下创建一个文件夹，文件夹名为problemName
        String path = "/app/localRun" + "/" + dirName;
        // 创建文件夹
        Path.get(path).toFile().mkdirs();
        // 将userFile保存到path目录下
        File userFilePath = new File(path + "/test." + language);
        userFile.transferTo(userFilePath);
        // 将inputFiles保存到path目录下
        if (inputFiles != null) {
            for (int i = 0; i < inputFiles.length; i++) {
                File inputFilePath = new File(path + "/in" + (i + 1) + ".txt");
                inputFiles[i].transferTo(inputFilePath);
            }
        } else throw new Exception("No input files found for problem");
        // 在该目录下新建out.txt文件
//        File outputFilePath = new File(path + "\\out.txt");
        File[] files = new File[inputFiles.length];
        for (int i = 0; i < inputFiles.length; i++) {
            File outputFilePath = new File(path + "/out" + (i + 1) + ".txt");
            if (!outputFilePath.createNewFile()) {
                throw new Exception("Failed to create output file at: " + outputFilePath.getAbsolutePath());
            }
            files[i] = outputFilePath;
        }

        File errFile = new File(path + "/err.txt");
        if (!errFile.createNewFile()) {
            throw new Exception("Failed to create error file at: " + errFile.getAbsolutePath());
        }

        String runPath = "/app/run.c";
        File runFilePath = new File(path + "/run.c");
        if (!runFilePath.getParentFile().exists()) {
            runFilePath.getParentFile().mkdirs(); // 确保目标目录存在
        }
        Files.copy(Paths.get(runPath), Paths.get(runFilePath.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);

        // 编译run.c文件为run可执行文件
        ProcessBuilder compileProcessBuilder = new ProcessBuilder("gcc", runFilePath.getAbsolutePath(), "-o", path + "/run");
        compileProcessBuilder.directory(new File(path));
        Process compileProcess = compileProcessBuilder.start();
        // 等待编译完成, 或者超时
        boolean compiled = compileProcess.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
        if (!compiled) {
            compileProcess.destroy();
            throw new Exception("Compilation timed out after 10 seconds.");
        }

        // 执行编译后的可执行文件，先修改权限chmod +x /app/run
        ProcessBuilder chmodProcessBuilder = new ProcessBuilder("chmod", "+x", path + "/run");
        chmodProcessBuilder.directory(new File(path));
        Process chmodProcess = chmodProcessBuilder.start();
        // 等待chmod完成, 或者超时
        boolean chmodFinished = chmodProcess.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
        if (!chmodFinished) {
            chmodProcess.destroy();
            throw new Exception("chmod timed out after 10 seconds.");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(path + "/run", userFilePath.getAbsolutePath(), files[0].getAbsolutePath());
        processBuilder.directory(new File(path));
        Process process = processBuilder.start();
        // 等待执行完成, 或者超时
        boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
        if (!finished) {
            process.destroy();
            throw new Exception("Execution timed out after 10 seconds.");
        }

        String[] result = new String[inputFiles.length + 1];

        // 从err.txt中读取错误信息
        result[inputFiles.length] = Files.readString(errFile.toPath(), StandardCharsets.UTF_8);

        // 从所有out(i + 1).txt中读取字符串，返回这个字符串数组
        for (int i = 0; i < inputFiles.length; i++) {
            File outputFile = new File(path + "/out" + (i + 1) + ".txt");
            if (outputFile.exists()) {
                // 使用Files.readAllLines读取文件内容
                List<String> lines = Files.readAllLines(outputFile.toPath(), StandardCharsets.UTF_8);
                // 将文件内容拼接为一个字符串
                result[i] = String.join("\n", lines); // 修复了这里
            } else {
                throw new Exception("Output file " + outputFile.getAbsolutePath() + " does not exist.");
            }
        }

        // 删除临时目录
        deleteDirectory(path);
        return result;
    }
    private void deleteDirectory(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            directory.delete();
        }
    }
}
