package com.pinyougou.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/11/4 8:54 PM
 * @desc todo
 */
public class ItemAuditMessageListener extends AbstractAdaptableMessageListener {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] ids = (Long[]) objectMessage.getObject();
        for (Long id : ids) {
            getPageHtml(id);
        }

        System.out.println("生成静态页面完成!");

    }

    private void getPageHtml(Long id) {

        try {
            //获得freemarker模板
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            Map<String, Object> dataModel = new HashMap<>();
            Goods goods = goodsService.findGoodsByIdAndStatus(id, "1");
            dataModel.put("goods", goods.getGoods());
            dataModel.put("goodsDesc", goods.getGoodsDesc());
            dataModel.put("itemList", goods.getItemList());

            TbItemCat category1Id = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1", category1Id.getName());
            TbItemCat category2Id = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2", category2Id.getName());
            TbItemCat category3Id = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3", category3Id.getName());

            FileWriter fileWriter = new FileWriter(ITEM_HTML_PATH + id + ".html");

            template.process(dataModel, fileWriter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
