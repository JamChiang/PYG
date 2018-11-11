package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.OrderService;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private IdWorker idWorker;

    //redis中购物车列表的名称
    private static final String REDIS_CART_LIST = "CART_LIST";

    @Override
    public PageResult search(Integer page, Integer rows, TbOrder order) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(order.get***())){
            criteria.andLike("***", "%" + order.get***() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        PageInfo<TbOrder> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public String addOrder(TbOrder order) {

        String outTradeNo = "";

        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(REDIS_CART_LIST).get(order.getUserId());

        if (cartList != null && cartList.size() > 0) {

            double totalFee = 0.0;
            String orderIds = "";

            for (Cart cart : cartList) {
                long orderId = idWorker.nextId();
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(orderId);
                tbOrder.setCreateTime(new Date());
                tbOrder.setUpdateTime(tbOrder.getCreateTime());
                //未付款
                tbOrder.setStatus("1");
                tbOrder.setUserId(order.getUserId());
                tbOrder.setSellerId(order.getSellerId());

                tbOrder.setReceiverAreaName(order.getReceiverAreaName());
                tbOrder.setReceiver(order.getReceiver());
                tbOrder.setReceiverMobile(order.getReceiverMobile());

                tbOrder.setPaymentType(order.getPaymentType());

                //本笔订单的支付总金额
                double payment = 0.0;
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(orderId);
                    payment += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insertSelective(orderItem);
                }
                tbOrder.setPayment(BigDecimal.valueOf(payment));
                orderMapper.insertSelective(tbOrder);

                totalFee += payment;

                if (orderIds.length() > 0) {
                    orderIds += "," + orderId;
                } else {
                    orderIds += orderId;
                }

            }

            if ("1".equals(order.getPaymentType())) {
                outTradeNo = idWorker.nextId() + "";
                TbPayLog payLog = new TbPayLog();
                payLog.setOutTradeNo(outTradeNo);
                payLog.setCreateTime(new Date());
                payLog.setTotalFee((long) (totalFee * 100));
                payLog.setUserId(order.getUserId());
                payLog.setOrderList(orderIds);
                payLog.setPayType(order.getPaymentType());
                payLog.setTradeState("0");

                payLogMapper.insertSelective(payLog);
            }

            redisTemplate.boundHashOps(REDIS_CART_LIST).delete(order.getUserId());

        }
        return outTradeNo;
    }
}
