package com.pinyougou.user.service;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;

public interface UserService extends BaseService<TbUser> {

    PageResult search(Integer page, Integer rows, TbUser user);

    void sendSmsCode(String phone);

    boolean checkSmsCode(String phone, String smsCode);

    List<TbAddress> findAddressList(String username);
}