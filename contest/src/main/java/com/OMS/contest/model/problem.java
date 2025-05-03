package com.OMS.contest.model;

import lombok.Data;

@Data
public class problem {
    private int id;
    private String name;
    private int caseNum;
    private String description;
    private int difficulty;
    private int acceptance;
}
