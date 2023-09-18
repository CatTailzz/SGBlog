package com.sangeng.service;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/8/28 19:50
 **/
public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();

}
