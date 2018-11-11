package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemsSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author JW
 * @createTime 2018/10/30 9:04 PM
 * @desc todo
 */

@RequestMapping("/itemsSearch")
@RestController
public class ItemsSearchController {

    @Reference
    private ItemsSearchService itemsSearchService;

    @PostMapping("/search")
    public Map<String, Object> itemsSearch(@RequestBody Map<String, Object> searchMap) {

        return itemsSearchService.search(searchMap);
    }
}
