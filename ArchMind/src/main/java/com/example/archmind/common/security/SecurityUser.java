package com.example.archmind.common.security;

import com.example.archmind.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class SecurityUser implements UserDetails {
    private final User user;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 从数据库获取用户角色/权限
        // 这里简化处理，实际项目中应该从 user_roles 表查询
        // 例如：return user.getRoles().stream()
        //         .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        //         .collect(Collectors.toList());

        // 默认所有用户都有 USER 角色
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 获取用户 ID
     */
    public Long getUserId() {
        return user.getId();
    }
}
