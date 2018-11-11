package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/11/9 10:09 AM
 * @desc todo
 */
@RequestMapping("/cart")
@RestController
public class CartController {

    //cookie中购物车列表的名称
    private static final String PYG_CART_LIST = "PYG_CART_LIST";
    private static final int COOKIE_CART_LIST_MAX_AGE = 24 * 3600;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    @GetMapping("/getUsername")
    public Map<String, Object> getUsername() {
        Map<String, Object> resultMap = new HashMap<>();
        //如果未登录;那么获取到的 username 为:anonymousUser
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        resultMap.put("username", username);
        return resultMap;
    }

    /**
     * 查询购物车列表
     *
     * @return 购物车列表
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> cookie_list;
        String cookie = CookieUtils.getCookieValue(request, PYG_CART_LIST, true);
        if (!StringUtils.isEmpty(cookie)) {
            cookie_list = JSONArray.parseArray(cookie, Cart.class);
        } else {
            cookie_list = new ArrayList<>();
        }
        if ("anonymousUser".equals(username)) {
            //未登录,从cookie中获取购物车列表
            return cookie_list;
        } else {
            //已登录,从redis中获取购物车列表
            List<Cart> redis_list = cartService.findRedisListByUsername(username);
            if (cookie_list.size() > 0) {
                redis_list = cartService.mergeCartList(cookie_list, redis_list);
                cartService.addCartListToRedis(redis_list, username);
                CookieUtils.deleteCookie(request, response, PYG_CART_LIST);
            }
            return redis_list;
        }
    }

    /**
     * 实现登录\为登录下添加购物车列表
     *
     * @param itemId sku id
     * @param num    变化数量
     * @return 结果
     */
    @GetMapping("/addItemToCartList")
    public Result addItemToCartList(Long itemId, Integer num) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取购物车列表
            List<Cart> cartList = findCartList();
            //将购物车列表添加进cookie
            List<Cart> newCartList = cartService.addItemToCartList(cartList, itemId, num);
            if ("anonymousUser".equals(username)) {
                String cartJsonStr = JSON.toJSONString(newCartList);
                CookieUtils.setCookie(request, response, PYG_CART_LIST,
                        cartJsonStr, COOKIE_CART_LIST_MAX_AGE, true);
            } else {
                //已登录,添加进redies中
                cartService.addCartListToRedis(newCartList, username);
            }
            return Result.ok("修改购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改购物车失败");


    }
}
