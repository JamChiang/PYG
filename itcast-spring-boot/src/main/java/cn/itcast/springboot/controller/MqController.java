package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/11/5 4:07 PM
 * @desc todo
 */
@RequestMapping("/mq")
@RestController
public class MqController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("/send")
    public String sendMsg() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Leslie");
        map.put("id", 123L);

        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue", map);

        return "消息发送成功!";
    }

    @GetMapping("/sendSms")
    public String sendSms() {
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNum", "15627466169");
        map.put("signName", "JamChiang");
        map.put("templateCode", "SMS_150172943");
        map.put("templateParam", "{\"code\":\"123456\"}");

        jmsMessagingTemplate.convertAndSend("itcast_sms_queue", map);

        return "消息发送成功!";
    }
}
