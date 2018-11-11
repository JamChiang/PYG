package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/11/2 7:19 PM
 * @desc todo
 */
@RequestMapping("/test")
@RestController
public class PageTestController {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @GetMapping("/audit")
    public String audit(Long[] goodsIds) {
        for (Long id : goodsIds) {
            getPageHtml(id);
        }
        return "audit success";
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

    @GetMapping("/delete")
    public String delete(Long[] goodsIds) {
        for (Long id : goodsIds) {
            File file = new File(ITEM_HTML_PATH + id + ".html");
            if (file.exists()) {
                file.delete();
            }
        }

        return "delete success";
    }

}
