package com.example.archmind.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.archmind.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
//mapper作用是给者层贴标签，告诉springBoot这个是操作数据库的
//用于直接继承BaseMapper能够不写CRUD
public interface UserMapper extends BaseMapper <User> {
}
