package com.OMS.practice.model;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class problem {
    private int id;
    private String name;

    @TableField("case_num")
    private int caseNum;

    private String description;
    private int difficulty;
    private int acceptance;

    public problem() {}

    public problem(int id, String name, int caseNum, String description, int difficulty, int acceptance) {
        this.id = id;
        this.name = name;
        this.caseNum = caseNum;
        this.description = description;
        this.difficulty = difficulty;
        this.acceptance = acceptance;
    }
}
