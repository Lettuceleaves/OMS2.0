package com.OMS.contest.repos;

import com.OMS.contest.model.contest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface mybatisRepos extends BaseMapper<contest> {
}
