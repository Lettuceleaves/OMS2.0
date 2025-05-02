package com.OMS.contest.repos.impl;

public class sqlHelper {
    public String getProblemInfoByTableName(String tableName) {
        return "SELECT * FROM " + tableName;
    }
}
