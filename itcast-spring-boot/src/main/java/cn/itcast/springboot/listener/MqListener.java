package cn.itcast.springboot.listener;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author JW
 * @createTime 2018/11/5 4:23 PM
 * @desc todo
 */

@Component
public class MqListener {

    @JmsListener(destination = "spring.boot.map.queue")
    public void receiveMsg(Map<String, Object> map) {
        System.out.println(map);
    }
}
