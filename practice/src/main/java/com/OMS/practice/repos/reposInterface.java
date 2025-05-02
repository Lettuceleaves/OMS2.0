package com.OMS.practice.repos;

import com.OMS.practice.model.problem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface reposInterface {

    @Select("SELECT * FROM problems LIMIT 20 OFFSET #{offset}")
    public List<problem> getProblems(int offset);
}
