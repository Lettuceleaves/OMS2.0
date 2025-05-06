package com.OMS.practice.service;

import com.OMS.practice.model.problem;
import io.minio.errors.MinioException;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface practiceService {
    String submit(String problemName, byte[] userFile, boolean contestMode) throws Exception;
    List<problem> getProblemList(int page);
    String putProblem(problem newProblem) throws MinioException, IOException, NoSuchAlgorithmException, InvalidKeyException;
    String putProblemInfo(MultipartFile newProblemFile, String name) throws IOException, NoSuchAlgorithmException, InvalidKeyException, MinioException;
    String deleteProblemByName(String problemName);
    String deleteProblemById(int id);
    String updateProblemById(problem updatedProblem);
    String uploadCaseById(int problemId, byte[] caseFile, byte[] answerFile);
    String deleteCaseById(int problemId, int caseId);
}
