package com.example.archmind.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

//用于用户个人信息管理的场景，比如个人中心展示，修改资料，后台管理员查看用户详情等
@Data
@Builder
public class UserInfoResponse {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private LocalDateTime createTime;
}
