package com.pinyougou.search.activemq.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/11/4 7:09 PM
 * @desc todo
 */
public class ItemImportMessageListener extends AbstractAdaptableMessageListener {

    @Autowired
    private ItemsSearchService itemsSearchService;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        List<TbItem> itemList = JSON.parseArray(textMessage.getText(), TbItem.class);
        for (TbItem tbItem : itemList) {
            Map map = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(map);
        }
        itemsSearchService.importItemsList(itemList);
        System.out.println("______________solr同步索引库数据完成______________");
    }
}
