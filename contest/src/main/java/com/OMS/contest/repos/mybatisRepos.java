package com.OMS.contest.repos;

import com.OMS.contest.model.checkFile;
import com.OMS.contest.model.contest;
import com.OMS.contest.repos.impl.sqlHelper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface mybatisRepos {

    @Select("SELECT * FROM contest WHERE contestId = #{contestId}")
    @Results({
            @Result(property = "contestId", column = "contestId"),
            @Result(property = "name", column = "name"),
            @Result(property = "problemTableName", column = "problemTableName"),
            @Result(property = "organizerId", column = "organizer_id"),
            @Result(property = "organizerName", column = "organizer_name"),
            @Result(property = "participants", column = "participants"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "description", column = "description")
    })
    contest getContestInfoById(int id);

    @Select("SELECT * FROM contest WHERE name = #{name}")
    @Results({
            @Result(property = "contestId", column = "contestId"),
            @Result(property = "name", column = "name"),
            @Result(property = "problemTableName", column = "problemTableName"),
            @Result(property = "organizerId", column = "organizer_id"),
            @Result(property = "organizerName", column = "organizer_name"),
            @Result(property = "participants", column = "participants"),
            @Result(property = "startTime", column = "start_time"),
            @Result(property = "endTime", column = "end_time"),
            @Result(property = "description", column = "description")
    })
    contest getContestInfoByName(String name);

    @SelectProvider(type = sqlHelper.class, method = "getProblemInfoByTableName")
    List<checkFile> getProblemInfoByTableName(String tableName);
}
