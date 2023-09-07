package com.sangeng.service;

import com.sangeng.domain.ResponseResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/6 10:10
 **/
public interface UploadService {
    ResponseResult uploadImg(MultipartFile img);
}
