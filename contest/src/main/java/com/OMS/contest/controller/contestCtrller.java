package com.OMS.contest.controller;

import com.OMS.contest.model.contest;
import com.OMS.contest.service.contestService;
import com.OMS.contest.tool.ContestRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class contestCtrller {

    @Autowired
    private contestService service;

    @PostMapping("/contest/submit/{problemName}")
    public String submit(@PathVariable("problemName") String problemName, @RequestParam("userFile") MultipartFile userFile) throws Exception {
        return service.submit(problemName, userFile.getBytes());
    }

    @PutMapping("/contest/put")
    public String putContest(@RequestBody ContestRequest request) {
        contest newContest = request.getContest();
        int[] problemIds = request.getProblemIds();
        return service.putContest(newContest, problemIds);
    }

    @DeleteMapping("/contest/delete/id/{id}")
    public String deleteContestById(@PathVariable("id") int id) {
        return service.deleteContestById(id);
    }

    @DeleteMapping ("/contest/delete/name/{name}")
    public String deleteContestByName(@PathVariable("name") String name) {
        return service.deleteContestByName(name);
    }

    @PostMapping("/contest/update")
    public String updateContestById(@RequestBody ContestRequest request) {
        contest updatedContest = request.getContest();
        int[] problemIds = request.getProblemIds();
        return service.updateContest(updatedContest, problemIds);
    }

    @GetMapping("/contest/getInfo/{name}")
    public contest getContestInfo(@PathVariable("name") String name) {
        return service.getContestInfo(name);
    }
}
