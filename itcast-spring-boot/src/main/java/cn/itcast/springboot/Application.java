package cn.itcast.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author JW
 * @createTime 2018/11/5 3:48 PM
 * @desc todo
 */

/**
 * 如果是 spring boot 的引导类，需要添加@SpringBootApplication 注解
 * 默认将扫描该引导类及其子包里面的 spring 注解
 */

@SpringBootApplication
public class Application  {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
