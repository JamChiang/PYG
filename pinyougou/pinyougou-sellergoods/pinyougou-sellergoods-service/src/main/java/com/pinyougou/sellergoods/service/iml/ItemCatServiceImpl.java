package com.pinyougou.sellergoods.service.iml;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ItemCatService.class)
public class ItemCatServiceImpl extends BaseServiceImpl<TbItemCat> implements ItemCatService {

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbItemCat itemCat) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(itemCat.get***())){
            criteria.andLike("***", "%" + itemCat.get***() + "%");
        }*/

        List<TbItemCat> list = itemCatMapper.selectByExample(example);
        PageInfo<TbItemCat> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void deleteItemByIds(Long[] ids) {
        List<Long> list = Arrays.asList(ids);
        List<Long> list1 = new ArrayList<>();

        Example example = new Example(TbItemCat.class);
        for (Long id : ids) {
            TbItemCat param = new TbItemCat();
            param.setParentId(id);
            List<TbItemCat> itemCatList = findByWhere(param);
            if (itemCatList != null && itemCatList.size() > 0) {
                for (TbItemCat tbItemCat : itemCatList) {
                    list1.add(tbItemCat.getId());
                }
            }
        }
        boolean b = list1.addAll(list);

        example.createCriteria().andIn("parentId", list1);
        deleteByIds(ids);
        itemCatMapper.deleteByExample(example);
    }

}
