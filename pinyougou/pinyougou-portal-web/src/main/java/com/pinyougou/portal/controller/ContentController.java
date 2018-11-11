package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author JW
 * @createTime 2018/10/29 5:13 PM
 * @desc todo
 */

@RequestMapping("/content")
@RestController
public class ContentController {

    @Reference
    private ContentService contentService;

    @GetMapping("/findContentListByCategoryId")
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        return contentService.findContentListByCategoryId(categoryId);
    }
}
