package com.pinyougou.service;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.vo.PageResult;

public interface OrderService extends BaseService<TbOrder> {

    PageResult search(Integer page, Integer rows, TbOrder order);

    String addOrder(TbOrder order);
}