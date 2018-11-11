package com.pinyougou.manage.controller;

import com.pinyougou.common.util.FastDFSClient;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author JW
 * @createTime 2018/10/24 4:36 PM
 * @desc todo
 */
@RequestMapping("/upload")
@RestController
public class UploadController {

    @PostMapping
    public Result upload(MultipartFile file) {

        try {
            //获得文件后缀
            String file_ext_name = file.getOriginalFilename().substring(file.getOriginalFilename().indexOf(".") + 1);

            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");
            String url = fastDFSClient.uploadFile(file.getBytes(), file_ext_name);
            return Result.ok(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("上传图片失败");
    }
}
