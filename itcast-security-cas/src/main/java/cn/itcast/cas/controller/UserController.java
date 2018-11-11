package cn.itcast.cas.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author JW
 * @createTime 2018/11/8 7:25 PM
 * @desc todo
 */
@RequestMapping("/user")
@RestController
public class UserController {

    @GetMapping("/getUsername")
    public String getUsername() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return name;
    }
}
