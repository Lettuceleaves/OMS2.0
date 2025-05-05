package com.OMS.practice.service.impl;

import com.OMS.practice.client.runClient;
import com.OMS.practice.model.problem;
import com.OMS.practice.repos.minioRepos;
import com.OMS.practice.repos.mybatisRepos;
import com.OMS.practice.service.practiceService;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Service
public class practiceServiceImpl implements practiceService {

    @Autowired
    private mybatisRepos mybatisRepos;

    @Autowired
    private minioRepos minioRepos;

    @Autowired
    private runClient runClient;

    @Override
    public List<problem> getProblemList(int page) {
        return mybatisRepos.getProblems(page);
    }

    @Override
    public String submit(String problemName, byte[] userFile) throws Exception {
        problem problemInfo = mybatisRepos.getProblemByName(problemName);
        if (problemInfo == null) throw new RuntimeException("Problem not found");
        int caseNum = problemInfo.getCaseNum();
        List<byte[]> cases = new java.util.ArrayList<>();
        cases.add(userFile);
        System.out.println("------- " + caseNum + " -------");
        for (int i = 0; i < caseNum; i++) {
            byte[] caseFile = minioRepos.downloadFile("case", problemName + "/" + i + ".txt");
            cases.add(caseFile);
        }
        System.out.print(new String(userFile));
        List<byte[]> result = runClient.testFeign(cases);

        StringBuilder sb = new StringBuilder();
        if (result == null || result.isEmpty()) {
            return "No results returned";
        } else if (result.get(0).length != 0) {
            return "Error: " + new String(result.get(0));
        }
        for (int i = 1; i <= caseNum; i++) {
            System.out.println("case:" + i + ": " + new String(result.get(i)));
            sb.append(new String(result.get(i))).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String putProblem(problem newProblem) {
        try {
            mybatisRepos.insert(newProblem);
            minioRepos.newDirectory("case", newProblem.getName());
            minioRepos.newDirectory("answer", newProblem.getName());
            return "Problem added successfully"; // 打包成事务 TODO
        } catch (Exception e) {
            return "Error adding problem: " + e.getMessage();
        }
    }

    @Override
    public String deleteProblemByName(String problemName) {
        try {
            if (mybatisRepos.deleteProblemByName(problemName) != 1) {
                return "Problem not found";
            }
            minioRepos.deleteDirectory("case", problemName);
            minioRepos.deleteDirectory("answer", problemName);
            return "Problem deleted successfully";
        } catch (Exception e) {
            return "Error deleting problem: " + e.getMessage();
        }
    }

    @Override
    public String deleteProblemById(int id) {
        try {
            String problemName = mybatisRepos.getProblemById(id).getName();
            if (mybatisRepos.deleteById(id) != 1) {
                return "Problem not found";
            }
            minioRepos.deleteDirectory("case", problemName);
            minioRepos.deleteDirectory("answer", problemName);
            return "Problem deleted successfully";
        } catch (Exception e) {
            return "Error deleting problem: " + e.getMessage();
        }
    }

    @Override
    public String updateProblemByName(problem updatedProblem) {
        try {
            if (mybatisRepos.updateProblem(updatedProblem) != 1) {
                return "Problem not found";
            }
            return "Problem updated successfully";
        } catch (Exception e) {
            return "Error updating problem: " + e.getMessage();
        }
    }

    @Override
    public String updateProblemById(problem updatedProblem) {
        try {
            problem existingProblem = mybatisRepos.getProblemById(updatedProblem.getId());
            if (existingProblem == null) {
                return "Problem not found";
            }
            mybatisRepos.updateById(updatedProblem);
            return "Problem updated successfully";
        } catch (Exception e) {
            return "Error updating problem: " + e.getMessage();
        }
    }

    @Override
    public String uploadCaseById(int problemId, byte[] caseFile, byte[] answerFile) {
        try {
            problem problemInfo = mybatisRepos.getProblemById(problemId);
            if (problemInfo == null) {
                return "Problem not found";
            }
            String problemName = problemInfo.getName();
            // Save case file
            minioRepos.uploadFile("case", problemName + "/" + (problemInfo.getCaseNum()) + ".txt", caseFile);
            // Save answer file
            minioRepos.uploadFile("answer", problemName + "/" + (problemInfo.getCaseNum()) + ".txt", answerFile);
            // Update case number in the problem
            problemInfo.setCaseNum(problemInfo.getCaseNum() + 1);
            mybatisRepos.updateById(problemInfo);
            return "Case files uploaded successfully";
        } catch (IOException | MinioException | NoSuchAlgorithmException | InvalidKeyException e) {
            return "Error uploading case files: " + e.getMessage();
        }
    }

    @Override
    public String deleteCaseById(int problemId, int caseId) {
        try {
            problem problemInfo = mybatisRepos.getProblemById(problemId);
            if (problemInfo == null) {
                return "Problem not found";
            }
            String problemName = problemInfo.getName();
            // Delete case file
            minioRepos.deleteFile("case", problemName + "/" + caseId + ".txt");
            // Delete answer file
            minioRepos.deleteFile("answer", problemName + "/" + caseId + ".txt");
            // Update case number in the problem
            if (caseId < problemInfo.getCaseNum()) {
                problemInfo.setCaseNum(problemInfo.getCaseNum() - 1);
                mybatisRepos.updateById(problemInfo);
            }
            return "Case files deleted successfully";
        } catch (IOException | MinioException | NoSuchAlgorithmException | InvalidKeyException e) {
            return "Error deleting case files: " + e.getMessage();
        }
    }
}
