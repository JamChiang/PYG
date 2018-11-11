package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {

    PageResult search(Integer page, Integer rows, TbGoods goods);

    void addGoods(Goods goods);

    Goods findGoodsById(Long id);

    void updateGoods(Goods goods);

    void updateStatus(Long[] ids, String status);

    void deleteByGoodsId(Long[] ids);

    void updateMarketStatus(Long[] ids, String status);

    /**
     * 根据spu id集合和状态查询对应的sku商品列表
     * @param ids
     * @param s
     * @return
     */
    List<TbItem> findItemsByGoodsIdsAndStatus(Long[] ids, String s);

    Goods findGoodsByIdAndStatus(Long goodsId, String status);
}