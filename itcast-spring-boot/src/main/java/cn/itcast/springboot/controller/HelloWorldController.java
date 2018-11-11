package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

/**
 * @author JW
 * @createTime 2018/11/5 3:53 PM
 * @desc todo
 */
@RestController
public class HelloWorldController {

    @Autowired
    private Environment environment;

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!" + environment.getProperty("url");
    }

}
