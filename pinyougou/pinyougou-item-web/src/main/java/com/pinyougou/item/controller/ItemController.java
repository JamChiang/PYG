package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author JW
 * @createTime 2018/11/2 4:52 PM
 * @desc todo
 */
@Controller
public class ItemController {

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @GetMapping("/{goodsId}")
    public ModelAndView toItemPage(@PathVariable Long goodsId) {

        //通过goods id查找对应的sku
        Goods goods = goodsService.findGoodsByIdAndStatus(goodsId,"1");

        ModelAndView mv = new ModelAndView("item");
        mv.addObject("goods", goods.getGoods());
        mv.addObject("goodsDesc", goods.getGoodsDesc());

        TbItemCat category1Id = itemCatService.findOne(goods.getGoods().getCategory1Id());
        mv.addObject("itemCat1", category1Id.getName());
        TbItemCat category2Id = itemCatService.findOne(goods.getGoods().getCategory2Id());
        mv.addObject("itemCat2", category2Id.getName());
        TbItemCat category3Id = itemCatService.findOne(goods.getGoods().getCategory3Id());
        mv.addObject("itemCat3", category3Id.getName());

        mv.addObject("itemList", goods.getItemList());
        return mv;
    }
}
