package com.example.archmind.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.archmind.entity.Task;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TaskMapper extends BaseMapper <Task>{

}
