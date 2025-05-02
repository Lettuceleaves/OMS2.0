package com.OMS.contest.model;

import lombok.Data;

@Data
public class checkFile {
    private int problemId;
    private int contestId;
    private String name;
    private String path;
}