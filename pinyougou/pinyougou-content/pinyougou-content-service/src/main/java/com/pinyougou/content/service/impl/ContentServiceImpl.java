package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Autowired
    private ContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TbContent content) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbContent.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(content.get***())){
            criteria.andLike("***", "%" + content.get***() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        PageInfo<TbContent> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    private static final String REDIS_CONTENT = "content";

    @Override
    public List<TbContent> findContentListByCategoryId(Long id) {

        List<TbContent> list = null;

        //先从redis缓存中查找,如果缓存里没有就从数据库中查询并保存在缓存中
        try {
            list = (List<TbContent>) redisTemplate.boundHashOps(REDIS_CONTENT).get(id);
            if (list != null) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Example example = new Example(TbContent.class);
        example.createCriteria().andEqualTo("categoryId", "1")
                .andEqualTo("status", "1");
        example.orderBy("sortOrder").desc();
        list = contentMapper.selectByExample(example);

        try {
            redisTemplate.boundHashOps(REDIS_CONTENT).put(id, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //新增数据后,删除缓存
    public void add(TbContent content) {
        super.add(content);

        updateContentInRedisByCategoryId(content.getCategoryId());
    }

    //根据分类id清除redis里的缓存
    private void updateContentInRedisByCategoryId(Long categoryId) {
        try {
            redisTemplate.boundHashOps(REDIS_CONTENT).delete(categoryId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(TbContent content) {
        //先查找旧的广告内容
        TbContent oldContent = super.findOne(content.getId());

        super.update(content);
        if (!oldContent.getCategoryId().equals(content.getCategoryId())) {
            updateContentInRedisByCategoryId(oldContent.getCategoryId());
        }
        updateContentInRedisByCategoryId(content.getCategoryId());
    }

    public void deleteByIds(Serializable[] ids) {
        //清除redis缓存
        Example example = new Example(TbContent.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        List<TbContent> list = contentMapper.selectByExample(example);

        if (list != null && list.size() > 0) {
            for (TbContent tbContent : list) {
                updateContentInRedisByCategoryId(tbContent.getCategoryId());
            }
        }
        super.deleteByIds(ids);
    }
}
