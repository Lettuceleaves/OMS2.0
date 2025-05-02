package com.OMS.contest.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class contest {
    private int contestId;
    private String name;
    private String problemTableName;
    private int organizerId;
    private String organizerName;
    private int participants;
    private Timestamp startTime;
    private Timestamp endTime;
    private String description;
}
