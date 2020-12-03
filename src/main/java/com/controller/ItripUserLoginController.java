package com.controller;

import com.po.Dto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api")
public class ItripUserLoginController {
    @RequestMapping(value = "/dologin")
    public Dto dologin(HttpServletRequest request, HttpServletResponse response,String name,String password){
        System.out.println("dologin...........");
        System.out.println("name:"+name+",password:"+password);
        return null;
    }

}
