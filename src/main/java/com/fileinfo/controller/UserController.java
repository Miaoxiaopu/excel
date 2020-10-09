package com.fileinfo.controller;

import com.fileinfo.condition.FileCondition;
import com.fileinfo.utils.DatePattern;
import com.sun.xml.internal.ws.spi.db.DatabindingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping
public class UserController {
    @RequestMapping("login")
    public String login(Model model){
        model.addAttribute("data", new FileCondition());
        return "index.html";
    }
}
