package com.OMS.practice.controller;

import com.OMS.practice.client.adviceClient;
import com.OMS.practice.model.problem;
import com.OMS.practice.service.practiceService;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/practice")
public class practiceCtrller {

    @Autowired
    private practiceService practiceService;

    @Autowired
    private adviceClient client;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Practice Service!";
    }

    @GetMapping("/rootHello")
    public String rootHello() {
        return "Hello from Practice Service Root!";
    }

    @GetMapping("testAdvice")
    public Flux<ServerSentEvent<String>> getAdvice(@RequestParam("userFile") MultipartFile userFile) throws IOException {
        return client.advice(new String(userFile.getBytes()));
    }

    @PostMapping("/submit/{problemName}")
    public String submit(@PathVariable("problemName") String problemName, @RequestParam("userFile") MultipartFile userFile) throws Exception {
        return practiceService.submit(problemName, userFile.getBytes(), false);
    }

    @PostMapping("/submitFeign/{problemName}")
    public String submitFeign(@PathVariable("problemName") String problemName, @RequestParam("userFile") byte[] userFile) throws Exception {
        return practiceService.submit(problemName, userFile, true);
    }

    @PutMapping("put")
    public String putProblem(@RequestBody problem newProblem) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        return practiceService.putProblem(newProblem);
    }

    @PutMapping("infomation/{name}")
    public String putProblemInformation(@RequestBody MultipartFile newProblemFile, @PathVariable("name") String name) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        return practiceService.putProblemInfo(newProblemFile, name);
    }

    @DeleteMapping("delete/name/{name}")
    public String deleteProblemByName(@PathVariable("name") String name) {
        return practiceService.deleteProblemByName(name);
    }

    @DeleteMapping("delete/id/{id}")
    public String deleteProblemById(@PathVariable("id") int id) {
        return practiceService.deleteProblemById(id);
    }

    @DeleteMapping("information/{name}")
    public String deleteProblemInformationByName(@PathVariable("name") String name) {
        try {
            return practiceService.deleteProblemByName(name);
        } catch (Exception e) {
            return "Error deleting problem information: " + e.getMessage();
        }
    }

    @PostMapping("update/id/{id}")
    public String updateProblemById(@RequestBody problem updatedProblem) {
        return practiceService.updateProblemById(updatedProblem);
    }

    @GetMapping("list/{page}") // 页数逻辑还有问题 TODO
    public List<problem> getProblemList(@PathVariable int page) {
        return practiceService.getProblemList(page);
    }

    @PostMapping("uploadCase/{problemId}")
    public String uploadCaseById(@PathVariable("problemId") int problemId,
                                 @RequestParam("caseFile") MultipartFile caseFile,
                                 @RequestParam("answerFile") MultipartFile answerFile) {
        try {
            return practiceService.uploadCaseById(problemId, caseFile.getBytes(), answerFile.getBytes());
        } catch (Exception e) {
            return "Error uploading case: " + e.getMessage();
        }
    }

    @DeleteMapping("deleteCase/{problemId}/{caseId}")
    public String deleteCaseById(@PathVariable("problemId") int problemId,
                                 @PathVariable("caseId") int caseId) {
        try {
            return practiceService.deleteCaseById(problemId, caseId);
        } catch (Exception e) {
            return "Error deleting case: " + e.getMessage();
        }
    }

}
