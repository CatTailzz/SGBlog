package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.vo.UserInfoAndRoleIdsVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.service.RoleService;
import com.sangeng.service.UserService;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.awt.print.PrinterAbortException;
import java.util.List;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/18 15:01
 **/
@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult list(User user, Integer pageNum, Integer pageSize){
        return userService.selectUserPage(user, pageNum, pageSize);
    }

    @PostMapping
    public ResponseResult add(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (!userService.checkUserNameUnique(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (!userService.checkPhoneUnique(user)){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        if (!userService.checkEmailUnique(user)){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        return userService.add(user);
    }

    @DeleteMapping("/{id}")
    public ResponseResult remove(@PathVariable Long id){
        if(id.equals(SecurityUtils.getUserId())){
            return ResponseResult.errorResult(500,"不能删除当前用户");
        }
        userService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult getinfo(@PathVariable Long id){
        //需要用户的角色id列表
        List<Long> userRoleIds = roleService.selectRoleIdByUserId(id);
        //所有角色id
        List<Role> roles = roleService.selectRoleAll();
        //用户的信息
        User user = userService.getById(id);

        UserInfoAndRoleIdsVo vo = new UserInfoAndRoleIdsVo(user, roles, userRoleIds);
        return ResponseResult.okResult(vo);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody User user){
        userService.updateUser(user);
        return ResponseResult.okResult();
    }
}
