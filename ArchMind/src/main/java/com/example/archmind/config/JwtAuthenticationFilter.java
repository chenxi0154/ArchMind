package com.example.archmind.config;

import com.example.archmind.common.util.JwtUtil;
import com.example.archmind.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private AuthService authService;
    private JwtUtil jwtUtil;
    private UserDetailsService userDetailsService;

    // 白名单路径（不需要认证）

    private static final String[] WHITE_LIST = {

            "/api/auth/login",

            "/api/auth/register",

            "/api/auth/refresh",

            "/api/auth/captcha",

            "/error",

            "/swagger-ui/**",

            "/v3/api-docs/**"

    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)throws ServletException, IOException
            {
    String  requestPath = request.getRequestURI();
//    检查是否存在于白名单
    if (isWhiteList(requestPath)){
        filterChain.doFilter(request,response);
        return;
        }
//    从请求中获取token
    String token = getTokenFromRequest(request);
    if (token==null){
        filterChain.doFilter(request,response);
        return;
        }

    try{

        boolean isValid = authService.validateTokenFromRedis(token);
        if(!isValid){

            filterChain.doFilter(request,response);
            return;
        }

        String username =jwtUtil.getUsernameFromToken(token);
        if (username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){

//            用于加载用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

//            创建认证对象
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 7. 设置认证信息到上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (JwtException e){
        log.warn("解析失败",e.getMessage());
        }catch (Exception e){
        log.error("认证过程异常",e);
        }
        filterChain.doFilter(request,response);
    }
//    从请求中获取Token
    private String getTokenFromRequest(HttpServletRequest request){
//        先从Authorization里拿到token 也就是从请求头里拿
        String bearerToken = request.getHeader("Authorization");
//是否为空，是否以bearer开头
        if(bearerToken!=null&& bearerToken.startsWith("Bearer")){
//            截取从第7为开始到结尾的字符串
            return bearerToken.substring(7);
        }
//       从参数中拿
        String token = request.getParameter("accessToken" );
        if (token!= null&& !token.isEmpty()){
            return token;
        }

        return null;
    }

//    遍历白色名单每一条放行路径
    private boolean isWhiteList(String path){
        for (String whitePath : WHITE_LIST) {
            // 把配置里的 ** 替换成正则 .*（匹配任意字符），用正则完整匹配当前请求路径
            if (path.matches(whitePath.replace("**", ".*"))) {
                return true; // 匹配上，直接放行，不需要校验token
            }
            // 把 /** 删掉，判断当前路径是否以白名单前缀开头
            if (path.startsWith(whitePath.replace("/**", ""))) {
                return true; // 前缀命中，放行
            }
        }
        return false;
    }
}


























