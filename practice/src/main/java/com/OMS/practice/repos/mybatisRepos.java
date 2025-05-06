package com.OMS.practice.repos;

import com.OMS.practice.model.problem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface mybatisRepos extends BaseMapper<problem> {

    @Select("SELECT * FROM problem WHERE id = #{id}")
    problem getProblemById(int id);

    @Delete("DELETE FROM problem WHERE name = #{name}")
    int deleteProblemByName(String name);

    @Update("UPDATE problem SET name = #{name}, case_num = #{caseNum}, description = #{description}, difficulty = #{difficulty}, acceptance = #{acceptance} WHERE id = #{id}")
    int updateProblem(problem prob);

    @Select("SELECT * FROM problem LIMIT 20 OFFSET #{offset * 20}")
    List<problem> getProblems(int offset);

    @Select("SELECT * FROM problem WHERE name = #{name}")
    problem getProblemByName(String name);
}