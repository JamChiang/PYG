package com.pinyougou.vo;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

/**
 * @author JW
 * @createTime 2018/10/24 2:17 PM
 * @desc todo
 */
public class Goods implements Serializable {
    private TbGoods goods;
    private TbGoodsDesc goodsDesc;
    private List<TbItem> itemList;

    public TbGoods getGoods() {
        return goods;
    }

    public void setGoods(TbGoods goods) {
        this.goods = goods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TbItem> itemList) {
        this.itemList = itemList;
    }
}
