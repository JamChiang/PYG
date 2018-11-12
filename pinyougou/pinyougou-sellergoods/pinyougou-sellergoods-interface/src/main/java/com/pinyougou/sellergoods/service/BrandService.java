package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.order.service.BaseService;
import com.pinyougou.vo.PageResult;

import java.util.List;
import java.util.Map;

public interface BrandService extends BaseService<TbBrand> {
    List<TbBrand> queryAll();

    List<TbBrand> testPage(Integer page, Integer rows);

    /**
     * 根据条件查询
     * @param tbBrand 查询条件对象
     * @param page 页号
     * @param rows 页大小
     * @return
     */
    public PageResult search(TbBrand tbBrand, Integer page, Integer rows);

    List<Map<String, Object>> selectOptionList();
}
