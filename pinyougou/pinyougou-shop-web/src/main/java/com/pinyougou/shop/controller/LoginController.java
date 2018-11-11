package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/10/22 5:24 PM
 * @desc todo
 */

@RequestMapping("/login")
@RestController
public class LoginController {

    @GetMapping("/getUserName")
    public Map<String, String> getUserName() {

        Map<String, String> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username", username);
        return map;
    }
}
