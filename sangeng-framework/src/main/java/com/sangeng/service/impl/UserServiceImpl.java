package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.User;
import com.sangeng.domain.entity.UserRole;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.domain.vo.UserInfoVo;
import com.sangeng.domain.vo.UserVo;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.mapper.UserMapper;
import com.sangeng.mapper.UserRoleMapper;
import com.sangeng.service.UserRoleService;
import com.sangeng.service.UserService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.awt.print.PrinterAbortException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-09-03 15:28:03
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public ResponseResult userInfo() {
        //获取当前id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成userinfovo
        UserInfoVo vo = BeanCopyUtils.copyBean(user, UserInfoVo.class);

        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        updateById(user);
        return ResponseResult.okResult();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult register(User user) {
        //非空判断
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        //存在判断
        if(userNameExists(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExists(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        //密码加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult selectUserPage(User user, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper();
        wrapper.like(StringUtils.hasText(user.getUserName()),User::getUserName, user.getUserName());
        wrapper.eq(StringUtils.hasText(user.getPhonenumber()),User::getPhonenumber, user.getPhonenumber());
        wrapper.eq(StringUtils.hasText(user.getStatus()),User::getStatus, user.getStatus());

        Page<User> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, wrapper);

        List<User> users = page.getRecords();
        List<UserVo> collect = users.stream()
                .map(u -> BeanCopyUtils.copyBean(u, UserVo.class))
                .collect(Collectors.toList());

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page.getTotal());
        pageVo.setRows(collect);
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getUserName,userName))==0;
    }

    @Override
    public boolean checkPhoneUnique(User user) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getPhonenumber,user.getPhonenumber()))==0;
    }

    @Override
    public boolean checkEmailUnique(User user) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getEmail,user.getEmail()))==0;
    }

    @Override
    @Transactional
    public ResponseResult add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
        if(user.getRoleIds() != null && user.getRoleIds().length > 0){
            insertUserRole(user);
        }
        return ResponseResult.okResult();
    }
    @Autowired
    private UserRoleService userRoleService;

    @Override
    @Transactional
    public void updateUser(User user) {
        LambdaQueryWrapper<UserRole> userRoleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userRoleLambdaQueryWrapper.eq(UserRole::getUserId, user.getId());
        userRoleService.remove(userRoleLambdaQueryWrapper);

        insertUserRole(user);
        updateById(user);
    }


    private void insertUserRole(User user) {
        List<UserRole> userRoles = Arrays.stream(user.getRoleIds())
                .map(roleId -> new UserRole(user.getId(), roleId))
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoles);
    }

    private boolean nickNameExists(String nickName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName, nickName);
        return count(queryWrapper) > 0;
    }

    private boolean userNameExists(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        return count(queryWrapper) > 0;
    }
}
