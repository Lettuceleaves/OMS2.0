package com.OMS.practice.repos;

import com.OMS.practice.model.problem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface mybatisRepos extends BaseMapper<problem> {

    @Select("SELECT * FROM problems LIMIT 20 OFFSET #{offset}")
    List<problem> getProblems(int offset);

    @Select("SELECT * FROM problems WHERE name = #{name}")
    problem getProblemByName(String name);
}
