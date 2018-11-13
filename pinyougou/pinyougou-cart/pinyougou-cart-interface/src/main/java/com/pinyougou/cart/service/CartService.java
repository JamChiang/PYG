package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

/**
 * @author JW
 * @createTime 2018/11/9 6:24 PM
 * @desc todo
 */
public interface CartService {

    /**
     * 根据商品id查询商品和数量添加进购物车列表
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    List<Cart> findRedisListByUsername(String username);

    void addCartListToRedis(List<Cart> newCartList, String username);

    List<Cart> mergeCartList(List<Cart> cookie_list, List<Cart> redis_list);

    void orderAccount(Long[] selectedItemIds, String username);

    List<Cart> findOrderAccount(String username);
}
