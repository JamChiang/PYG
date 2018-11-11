package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;
import java.io.Serializable;

/**s
 * @author JW
 * @createTime 2018/11/4 9:12 PM
 * @desc todo
 */
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] ids = (Long[]) objectMessage.getObject();
        for (Long id : ids) {
            File file = new File(ITEM_HTML_PATH + id + ".html");
            if (file.exists()) {
                file.delete();
            }
        }
        System.out.println("删除静态页面资源完成!");
    }
}
