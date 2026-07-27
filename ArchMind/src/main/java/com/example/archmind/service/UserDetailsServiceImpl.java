package com.example.archmind.service;

import com.example.archmind.common.security.SecurityUser;
import com.example.archmind.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        User user = userService.getByUsername(username);

        if (user ==null){
            throw new UsernameNotFoundException("用户不存在"+username);
        }

        return new SecurityUser(user);

    }

}
