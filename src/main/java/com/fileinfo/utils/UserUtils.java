package com.fileinfo.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fileinfo.entity.GovUserEntity;
import com.fileinfo.service.IGovUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
public class UserUtils {

    public static void authenticate(UsernamePasswordToken usernamePasswordToken) {
        try {
            SecurityUtils.getSubject().login(usernamePasswordToken);
        } catch (UnknownAccountException e) {
            log.error("用户名错误！ {}", e.getMessage());
            throw e;
        } catch (AuthenticationException e) {
            log.error("密码错误！ {}", e.getMessage());
            throw e;
        }
    }

    public static void logout() {
        SecurityUtils.getSubject().logout();
    }
}
