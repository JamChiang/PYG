package com.pinyougou.search.activemq.listener;

import com.pinyougou.search.service.ItemsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.Serializable;

/**
 * @author JW
 * @createTime 2018/11/4 7:30 PM
 * @desc todo
 */
public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemsSearchService itemsSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] ids = (Long[]) objectMessage.getObject();
        itemsSearchService.deleteItemsByGoodsIds(ids);

        System.out.println("______________solr删除索引库数据完成______________");
    }
}
