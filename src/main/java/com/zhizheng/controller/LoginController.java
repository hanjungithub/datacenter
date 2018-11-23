package com.zhizheng.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class LoginController {


    @RequestMapping("/login")
    public ModelAndView login(String username,String password){
        UsernamePasswordToken passwordToken = new UsernamePasswordToken(username,
                password, false);
        Subject subject = SecurityUtils.getSubject();
        subject.login(passwordToken);
        ModelAndView modelAndView = new ModelAndView("test");
        return modelAndView;
    }

    @RequestMapping("/testgo")
    public ModelAndView testgo(){
        ModelAndView modelAndView = new ModelAndView("testgo");
        return modelAndView;
    }

}
