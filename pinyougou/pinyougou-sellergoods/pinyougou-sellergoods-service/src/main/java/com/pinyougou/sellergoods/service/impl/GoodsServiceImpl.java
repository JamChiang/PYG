package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult search(Integer page, Integer rows, TbGoods goods) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("isDelete", "1");
//        criteria.andNotEqualTo("isMarketable", "0");

        if (!StringUtils.isEmpty(goods.getSellerId())) {
            criteria.andEqualTo("sellerId", goods.getSellerId());
        }
        if (!StringUtils.isEmpty(goods.getAuditStatus())) {
            criteria.andEqualTo("auditStatus", goods.getAuditStatus());
        }
        if (!StringUtils.isEmpty(goods.getGoodsName())) {
            criteria.andLike("goodsName", "%" + goods.getGoodsName() + "%");
        }
        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {

//        goodsMapper.insertSelective(goods.getGoods());

        add(goods.getGoods());

        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());

        goodsDescMapper.insertSelective(goods.getGoodsDesc());

        saveItemList(goods);

    }

    @Override
    public Goods findGoodsById(Long id) {
        Goods goods = new Goods();
        goods.setGoods(findOne(id));
        goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));

        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId", id);
        goods.setItemList(itemMapper.selectByExample(example));
        return goods;
    }

    @Override
    public void updateGoods(Goods goods) {
        //更新信息,状态修改为未审核
        goods.getGoods().setAuditStatus("0");
        goods.getGoods().setAuditStatus("0");

        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

//        Example example = new Example(TbItem.class);
//        example.createCriteria().andEqualTo("goodsId", goods.getGoods().getId());
//        itemMapper.deleteByExample(example);
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);

        saveItemList(goods);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));

        TbGoods param = new TbGoods();
        param.setAuditStatus(status);

        goodsMapper.updateByExampleSelective(param, example);

        if ("2".equals(status)) {
            TbItem item = new TbItem();
            item.setStatus("1");

            Example example1 = new Example(TbItem.class);
            example1.createCriteria().andIn("goodsId", Arrays.asList(ids));

            itemMapper.updateByExampleSelective(item, example1);
        }
    }

    @Override
    public void deleteByGoodsId(Long[] ids) {
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));

        TbGoods param = new TbGoods();
        param.setIsDelete("1");
        goodsMapper.updateByExampleSelective(param, example);
    }

    @Override
    public void updateMarketStatus(Long[] ids, String status) {
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids))
                .andEqualTo("auditStatus","2");

        TbGoods param = new TbGoods();
        param.setIsMarketable(status);

        goodsMapper.updateByExampleSelective(param, example);

    }

    @Override
    public List<TbItem> findItemsByGoodsIdsAndStatus(Long[] ids, String s) {
        Example example = new Example(TbItem.class);
        example.createCriteria().andIn("goodsId", Arrays.asList(ids))
                .andEqualTo("status", s);
        return  itemMapper.selectByExample(example);
    }

    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String status) {
        Goods goods = new Goods();

        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        goods.setGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        goods.setGoodsDesc(tbGoodsDesc);

        Example example = new Example(TbItem.class);
        example.createCriteria().andEqualTo("goodsId", goodsId)
                .andEqualTo("status", status);
        example.orderBy("isDefault").desc();
        List<TbItem> tbItemList = itemMapper.selectByExample(example);
        goods.setItemList(tbItemList);

        return goods;
    }


    private void saveItemList(Goods goods) {

        if ("1".equals(goods.getGoods().getIsEnableSpec())) {

            for (TbItem tbItem : goods.getItemList()) {
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> map = JSON.parseObject(tbItem.getSpec());
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    title += " " + entry.getValue().toString();
                }
                tbItem.setTitle(title);

                setItemValue(tbItem, goods);
                itemMapper.insertSelective(tbItem);
            }

        } else {
            TbItem tbItem = new TbItem();
            tbItem.setTitle(goods.getGoods().getGoodsName());
            tbItem.setPrice(goods.getGoods().getPrice());
            tbItem.setNum(9999);
            tbItem.setStatus("0");
            tbItem.setIsDefault("1");
            tbItem.setSpec("{}");

            setItemValue(tbItem, goods);

            itemMapper.insertSelective(tbItem);
        }
    }

    private void setItemValue(TbItem tbItem, Goods goods) {
        //图片
        List<Map> imgList = JSONArray.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);

        if (imgList != null && imgList.size() > 0) {
            //将照片的第一张作为sku封面
            tbItem.setImage(imgList.get(0).get("url").toString());

        }
        //商品分类ID
        tbItem.setCategoryid(goods.getGoods().getCategory3Id());
        //商品分类名字
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        tbItem.setCategory(itemCat.getName());

        //创建时间
        tbItem.setCreateTime(new Date());
        //更新时间
        tbItem.setUpdateTime(tbItem.getCreateTime());
        //设置SPU id
        tbItem.setGoodsId(goods.getGoods().getId());
        //设置商家 id
        tbItem.setSellerId(goods.getGoods().getSellerId());
        //设置商家名称
        TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        tbItem.setSeller(tbSeller.getName());

        //设置品牌名称
        TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        tbItem.setBrand(tbBrand.getName());
    }
}
