package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

/**
 * @author JW
 * @createTime 2018/10/30 9:12 PM
 * @desc todo
 */
@Service(interfaceClass = ItemsSearchService.class)
public class ItemsSearchServiceImpl implements ItemsSearchService {

    @Autowired
    private SolrTemplate solrTemplate;


    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        //处理关键字中空格问题
        if (!StringUtils.isEmpty(searchMap.get("keywords"))) {
            searchMap.put("keywords", searchMap.get("keywords").toString().replaceAll(" ", ""));
        }

        //创建高亮搜索对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();

        //item_keywords对应schema.xml配置文件里面的多值项
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        //按照分类进行筛选
        if (!StringUtils.isEmpty(searchMap.get("category"))) {
            SimpleQuery simpleQuery = new SimpleQuery();
            Criteria criteriaFilter = new Criteria("item_category").is(searchMap.get("category"));
            simpleQuery.addCriteria(criteriaFilter);
            query.addFilterQuery(simpleQuery);
        }
        //按照品牌进行筛选
        if (!StringUtils.isEmpty(searchMap.get("brand"))) {
            SimpleQuery simpleQuery = new SimpleQuery();
            Criteria criteriaFilter = new Criteria("item_brand").is(searchMap.get("brand"));
            simpleQuery.addCriteria(criteriaFilter);
            query.addFilterQuery(simpleQuery);
        }
        //按照规格进行筛选
        if (searchMap.get("spec") != null) {
            Map<String, Object> specMap = (Map<String, Object>) searchMap.get("spec");
            Set<Map.Entry<String, Object>> specSet = specMap.entrySet();
            for (Map.Entry<String, Object> entry : specSet) {
                SimpleQuery simpleQuery = new SimpleQuery();
                Criteria criteriaFilter = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                simpleQuery.addCriteria(criteriaFilter);
                query.addFilterQuery(simpleQuery);
            }
        }

        //按价格区间进行筛选
        if (!StringUtils.isEmpty(searchMap.get("price"))) {
            String[] prices = searchMap.get("price").toString().split("-");

            Criteria greaterThanPrice = new Criteria("item_price").greaterThanEqual(prices[0]);
            SimpleQuery greaterThanPriceQuery = new SimpleQuery(greaterThanPrice);
            query.addFilterQuery(greaterThanPriceQuery);

            if (!"*".equals(prices[1])) {
                Criteria lessThanPrice = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleQuery lessThanPriceQuery = new SimpleQuery(lessThanPrice);
                query.addFilterQuery(lessThanPriceQuery);
            }
        }

        //设置排序
        if (!StringUtils.isEmpty(searchMap.get("sortField")) && !StringUtils.isEmpty(searchMap.get("sort"))) {

            Sort sort = new Sort(searchMap.get("sort").toString().equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "item_" + searchMap.get("sortField"));
            query.addSort(sort);
        }

        //设置分页信息
        Integer pageNo = 1;
        Integer pageSize = 40;

        if (searchMap.get("pageNo") != null) {
            pageNo = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        if (searchMap.get("pageSize") != null) {
            pageSize = Integer.parseInt(searchMap.get("pageSize").toString());
        }
        query.setOffset(pageNo);
        query.setRows(pageSize);

        //设置高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");
        highlightOptions.setSimplePrefix("<em style ='color:red'>");
        highlightOptions.setSimplePostfix("</em>");
        query.setHighlightOptions(highlightOptions);

        //查询
        HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);

        //处理高亮标题
        List<HighlightEntry<TbItem>> highlighted = itemHighlightPage.getHighlighted();

        if (highlighted.size() > 0) {
            for (HighlightEntry<TbItem> entry : highlighted) {
                List<HighlightEntry.Highlight> highlights = entry.getHighlights();

                if (highlights != null &&
                        highlights.size() > 0 &&
                        highlights.get(0).getSnipplets() != null) {
                    entry.getEntity().setTitle(highlights.get(0).getSnipplets().get(0));
                }

            }
        }

        resultMap.put("rows", itemHighlightPage.getContent());
        resultMap.put("total", itemHighlightPage.getTotalElements());
        resultMap.put("totalPages", itemHighlightPage.getTotalPages());

        return resultMap;
    }

    @Override
    public void importItemsList(List<TbItem> itemList) {

        for (TbItem item : itemList) {
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteItemsByGoodsIds(Long[] ids) {
        Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(ids));
        SimpleQuery query = new SimpleQuery(criteria);

        solrTemplate.delete(query);
        solrTemplate.commit();
    }
}
