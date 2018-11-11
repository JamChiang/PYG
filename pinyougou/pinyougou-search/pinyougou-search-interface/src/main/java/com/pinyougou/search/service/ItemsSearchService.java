package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @author JW
 * @createTime 2018/10/30 9:11 PM
 * @desc todo
 */
public interface ItemsSearchService {
    Map<String, Object> search(Map<String, Object> searchMap);

    void importItemsList(List<TbItem> itemList);

    void deleteItemsByGoodsIds(Long[] ids);
}
