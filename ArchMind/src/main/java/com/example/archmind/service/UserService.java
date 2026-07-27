package com.example.archmind.service;

import com.example.archmind.dto.request.RegisterRequest;
import com.example.archmind.entity.User;

public interface UserService {

//写方法是 类型名＋方法名
//    获取的信息就是注册定义所需要获取的信息
    User UserRegister(RegisterRequest request);

    boolean existsByUsername(String username);


    User getByUsername(String name);


   void updateLoginInfo(Long userId,String ip);

}
