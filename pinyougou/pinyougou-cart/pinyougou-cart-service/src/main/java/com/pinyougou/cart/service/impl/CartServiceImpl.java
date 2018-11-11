package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JW
 * @createTime 2018/11/9 6:26 PM
 * @desc todo
 */
@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    //redis中购物车列表的名称
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据商品id查询商品和数量添加进购物车列表
     *
     * @param cartList
     * @param itemId
     * @param num
     * @return
     */
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {

        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);

        if (tbItem == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(tbItem.getStatus())) {
            throw new RuntimeException("商品状态非法");
        }

        Cart cart = findCartBySellerId(cartList, tbItem.getSellerId());
        if (cart == null) {
            //商品不存在在商家对应的订单商品列表中,则创建一个订单商品并加入该商家的订单列表中
            if (num > 0) {
                cart = new Cart();
                cart.setSellerId(tbItem.getSellerId());
                cart.setSeller(tbItem.getSeller());

                List<TbOrderItem> orderItemList = new ArrayList<>();
                TbOrderItem orderItem = createOrderItem(itemId, num);
                orderItemList.add(orderItem);
                cart.setOrderItemList(orderItemList);
                cartList.add(cart);
            } else {
                throw new RuntimeException("商品数量非法");
            }
        } else {
            //商品存在在商家对应的订单商品列表中则叠加购买商品数量，
            TbOrderItem item = findOrderItemByItemId(cart, itemId);
            if (item != null) {
                item.setNum(item.getNum() + num);
                item.setTotalFee(BigDecimal.valueOf(item.getNum() * item.getPrice().doubleValue()));
                //如果购买数量小于1的话，需要将该商品从该商家的订单列表中删除；
                if (item.getNum() <= 0) {
                    cart.getOrderItemList().remove(item);
                }
                //如果该商家一个商品都没有了则需要将该商家从购物车列表中删除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            } else {
                //商品不存在在商家对应的订单商品列表中,则添加商品，
                if (num > 0) {
                    TbOrderItem orderItem = createOrderItem(itemId, num);
                    cart.getOrderItemList().add(orderItem);
                } else {
                    throw new RuntimeException("商品数量非法");
                }
            }

        }

        return cartList;
    }

    @Override
    public List<Cart> findRedisListByUsername(String username) {
        List<Cart> redis_list = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(username);
        if (redis_list != null) {
            return redis_list;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void addCartListToRedis(List<Cart> newCartList, String username) {
        redisTemplate.boundHashOps(REDIS_CART_LIST).put(username, newCartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookie_list, List<Cart> redis_list) {
        for (Cart cart : cookie_list) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                addItemToCartList(redis_list, orderItem.getItemId(), orderItem.getNum());
            }
        }

        return redis_list;
    }

    /**
     * 根据商品id,判断商品是否存在商家对应的订单列表中
     *
     * @param cart
     * @param itemId
     * @return
     */
    private TbOrderItem findOrderItemByItemId(Cart cart, Long itemId) {

        if (cart.getOrderItemList() != null && cart.getOrderItemList().size() > 0) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                if (itemId.equals(orderItem.getItemId())) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     * 根据商品id和数量,创建订单明细
     *
     * @param itemId
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(Long itemId, Integer num) {
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(itemId);
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setNum(num);
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setSellerId(tbItem.getSellerId());
        orderItem.setTitle(tbItem.getTitle());
        orderItem.setTotalFee(BigDecimal.valueOf(tbItem.getPrice().doubleValue() * num));
        return orderItem;
    }

    /**
     * 根据商家id在购物车列表中查询购物车
     *
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerId())) {
                return cart;
            }
        }
        return null;
    }
}
