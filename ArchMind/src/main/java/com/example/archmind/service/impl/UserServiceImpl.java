package com.example.archmind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.archmind.common.exception.BusinessException;
import com.example.archmind.dao.UserMapper;
import com.example.archmind.dto.request.RegisterRequest;
import com.example.archmind.entity.User;
import com.example.archmind.service.UserService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public class UserServiceImpl implements UserService {
    private UserMapper userMapper;

    @Override
    @Transactional
    public User UserRegister(RegisterRequest request){
        if (this.existsByUsername(request.getUsername())){
            throw new BusinessException("用户名已被使用");
        }
//        用户不存在啧需要创建用户存放的地方
        User user = new User();
//        然后读取参数存到这个实体类中
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
//        上面是将参数内容写入实体类中，然后将真整个实体类插入到userMapper对应的数据表中

        userMapper.insert(user);

        return user;
    }
@Override
    public boolean existsByUsername(String username){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
//根据用户名查询是否已存在该用户
    public User getByUsername(String name){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

//        翻译一下就是，根据username==name条件查询User表中所有的字段
        wrapper.eq(User::getUsername,name);

        return userMapper.selectOne(wrapper);
    }
@Override
    public void updateLoginInfo(Long userId,String ip){
        User user = new User();
        user.setId(userId);
        user.setLastLoginIp(ip);
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.insert(user);
    }
}
