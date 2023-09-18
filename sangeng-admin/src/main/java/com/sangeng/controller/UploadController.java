package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/15 13:32
 **/
@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upload")
    public ResponseResult upload(@RequestParam("img") MultipartFile multipartFile){
        try {
            return uploadService.uploadImg(multipartFile);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败");
        }
    }
}
