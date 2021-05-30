package com.fileinfo.controller;

import com.fileinfo.entity.UserInfo;
import com.fileinfo.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping
public class UserController {
    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }

    @RequestMapping("/index.html")
    public String mainPage() {
        return "index";
    }

    @RequestMapping("/login")
    public String login(@RequestBody UserInfo userInfo) {
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(userInfo.getUserName(), userInfo.getPassword());
        try {
            UserUtils.authenticate(usernamePasswordToken);
        } catch (AuthenticationException e) {
//            model.addAttribute("data", Result.fail("用户名错误！"));
            throw e;
        }//            model.addAttribute("data", Result.fail("密码错误！"));

        return "index";
    }

    @RequestMapping("/logout")
    public String logout(Model model) {
        UserUtils.logout();
        return "login";
    }

    @RequestMapping("/permission")
    @RequiresPermissions("user:create")
    public String testP() {
        return "perm";
    }
}
