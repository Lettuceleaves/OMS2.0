package com.OMS.practice.controller;

import com.OMS.practice.client.adviceClient;
import com.OMS.practice.model.problem;
import com.OMS.practice.service.practiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
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
        return practiceService.submit(problemName, userFile.getBytes());
    }

    @PostMapping("/submitFeign/{problemName}")
    public String submitFeign(@PathVariable("problemName") String problemName, @RequestParam("userFile") byte[] userFile) throws Exception {
        return practiceService.submit(problemName, userFile);
    }

    @PutMapping("put")
    public String putProblem(@RequestBody problem newProblem) {
        return practiceService.putProblem(newProblem);
    }

    @DeleteMapping("delete/name/{name}")
    public String deleteProblemByName(@PathVariable("name") String name) {
        return practiceService.deleteProblemByName(name);
    }

    @DeleteMapping("delete/id/{id}")
    public String deleteProblemById(@PathVariable("id") int id) {
        return practiceService.deleteProblemById(id);
    }

    @PostMapping("update/name/{name}") // 没有同步给minio文件夹改名
    public String updateProblemByName(@RequestBody problem updatedProblem) {
        return practiceService.updateProblemByName(updatedProblem);
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
