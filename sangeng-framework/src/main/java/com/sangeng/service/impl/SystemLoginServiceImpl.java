package com.sangeng.service.impl;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.LoginUser;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.BlogUserLoginVo;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.service.LoginService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.JwtUtil;
import com.sangeng.utils.RedisCache;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/8/28 19:50
 **/
@Service
public class SystemLoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());

        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        //判断是否认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid，生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userid = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userid);
        //存入redis
        redisCache.setCacheObject("login:" + userid, loginUser);
        Map<String,String> map = new HashMap<>();
        map.put("token",jwt);
        return ResponseResult.okResult(map);
    }

    @Override
    public ResponseResult logout() {
        //获取当前登陆的用户id
        Long userId = SecurityUtils.getUserId();
        //删除login：id的redis
        redisCache.deleteObject("login:" + userId);
        return ResponseResult.okResult();
    }

}
