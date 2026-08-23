package com.example.archmind.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.archmind.entity.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}
