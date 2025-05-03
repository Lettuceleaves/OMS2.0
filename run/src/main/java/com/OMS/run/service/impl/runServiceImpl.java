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
import java.util.*;

@Service
public class runServiceImpl implements runService {

    @Override
    public List<byte[]> run(byte[] userFile, String language, List<byte[]> inputFiles) throws Exception {
        // 创建临时目录
        String dirName = createTempDirectory();
        String path = "/app/localRun" + "/" + dirName;
        Path.get(path).toFile().mkdirs();

        // 保存用户文件和输入文件
        File userFilePath = saveUserFile(path, userFile, language);
        File[] inputFilePaths = saveInputFiles(path, inputFiles);

        // 创建输出文件和错误文件
        File[] outputFilePaths = createOutputFiles(path, inputFiles.size());
        File errFile = createErrorFile(path);

        // 复制运行脚本并编译
        File runFilePath = copyAndCompileRunScript(path);

        // 执行程序
        executeProgram(runFilePath, userFilePath, outputFilePaths);

        // 读取结果
        List<byte[]> result = readResults(outputFilePaths, errFile);

        // 删除临时目录
        deleteDirectory(path);

        return result;
    }

    private String createTempDirectory() {
        // 生成uuid加一串随机字符加时间戳
        return System.currentTimeMillis() + UUID.randomUUID().toString() + RandomStringUtils.randomAlphabetic(10);
    }

    private File saveUserFile(String path, byte[] userFile, String language) throws IOException {
        // 将userFile保存到path目录下
        File userFilePath = new File(path + "/test." + language);
        Files.write(userFilePath.toPath(), userFile);
        return userFilePath;
    }

    private File[] saveInputFiles(String path, List<byte[]> inputFiles) throws IOException {
        if (inputFiles == null) {
            throw new IllegalArgumentException("No input files found for problem");
        }
        File[] inputFilePaths = new File[inputFiles.size()];
        for (int i = 0; i < inputFiles.size(); i++) {
            byte[] inputFile = inputFiles.get(i);
            File inputFilePath = new File(path + "/in" + (i + 1) + ".txt");
            Files.write(inputFilePath.toPath(), inputFile);
            inputFilePaths[i] = inputFilePath;
        }
        return inputFilePaths;
    }

    private File[] createOutputFiles(String path, int count) throws IOException {
        File[] outputFilePaths = new File[count];
        for (int i = 0; i < count; i++) {
            File outputFilePath = new File(path + "/out" + (i + 1) + ".txt");
            if (!outputFilePath.createNewFile()) {
                throw new IOException("Failed to create output file at: " + outputFilePath.getAbsolutePath());
            }
            outputFilePaths[i] = outputFilePath;
        }
        return outputFilePaths;
    }

    private File createErrorFile(String path) throws IOException {
        File errFile = new File(path + "/err.txt");
        if (!errFile.createNewFile()) {
            throw new IOException("Failed to create error file at: " + errFile.getAbsolutePath());
        }
        return errFile;
    }

    private File copyAndCompileRunScript(String path) throws IOException, InterruptedException {
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
            throw new IOException("Compilation timed out after 10 seconds.");
        }
        return runFilePath;
    }

    private void executeProgram(File runFilePath, File userFilePath, File[] outputFilePaths) throws IOException, InterruptedException {
        // 修改权限
        ProcessBuilder chmodProcessBuilder = new ProcessBuilder("chmod", "+x", runFilePath.getParent() + "/run");
        chmodProcessBuilder.directory(runFilePath.getParentFile());
        Process chmodProcess = chmodProcessBuilder.start();
        // 等待chmod完成, 或者超时
        boolean chmodFinished = chmodProcess.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
        if (!chmodFinished) {
            chmodProcess.destroy();
            throw new IOException("chmod timed out after 10 seconds.");
        }

        // 执行程序
        ProcessBuilder processBuilder = new ProcessBuilder(runFilePath.getParent() + "/run", userFilePath.getAbsolutePath(), outputFilePaths[0].getAbsolutePath());
        processBuilder.directory(runFilePath.getParentFile());
        Process process = processBuilder.start();
        // 等待执行完成, 或者超时
        boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.SECONDS);
        if (!finished) {
            process.destroy();
            throw new IOException("Execution timed out after 10 seconds.");
        }
    }

    private List<byte[]> readResults(File[] outputFilePaths, File errFile) throws IOException {
        List<byte[]> result = new ArrayList<>();

        // 从err.txt中读取错误信息
        byte[] errContent = Files.readAllBytes(errFile.toPath());
        result.add(errContent);

        // 从所有out(i + 1).txt中读取字节数组
        for (File outputFile : outputFilePaths) {
            if (outputFile.exists()) {
                // 使用Files.readAllBytes读取文件内容为字节数组
                byte[] fileContent = Files.readAllBytes(outputFile.toPath());
                System.out.println(new String(fileContent));
                result.add(fileContent);
            } else {
                throw new IOException("Output file " + outputFile.getAbsolutePath() + " does not exist.");
            }
        }

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
