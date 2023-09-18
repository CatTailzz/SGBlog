package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Role;
import com.sangeng.domain.entity.RoleMenu;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.domain.vo.RoleVo;
import com.sangeng.mapper.RoleMapper;
import com.sangeng.service.RoleMenuService;
import com.sangeng.service.RoleService;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2023-09-13 10:54:00
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        List<String> roleKeys = new ArrayList<>();
        //判断是不是管理员
        if(id == 1L){
            roleKeys.add("admin");
            return roleKeys;
        }
        //如果是，那么集合中只需要有admin
        //否则，查询角色
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult selectRolePage(Integer pageNum, Integer pageSize, Role role) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        wrapper.like(StringUtils.hasText(role.getRoleName()),Role::getRoleName, role.getRoleName());
        wrapper.eq(StringUtils.hasText(role.getStatus()),Role::getStatus, role.getStatus());
        wrapper.orderByAsc(Role::getRoleSort);

        Page<Role> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, wrapper);

        List<Role> roles = page.getRecords();
        List<RoleVo> roleVos = BeanCopyUtils.copyBeanList(roles, RoleVo.class);

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page.getTotal());
        pageVo.setRows(roleVos);
        return ResponseResult.okResult(pageVo);

    }

    /**
     * 新增role，还要把role的menu关联加上，这里给role表加一个字段
     * @param role
     */
    @Override
    @Transactional
    public void addRole(Role role) {
        save(role);
        if(role.getMenuIds() != null && role.getMenuIds().length > 0)
            addRoleMenu(role);
    }

    @Override
    public void updateRole(Role role) {
        updateById(role);
        //更新关联表
        roleMenuService.deleteRoleMenuByRoleId(role.getId());
        addRoleMenu(role);
    }

    @Override
    public List<Role> selectRoleAll() {
        return list(Wrappers.<Role>lambdaQuery().eq(Role::getStatus, SystemConstants.STATUS_NORMAL));
    }

    @Override
    public List<Long> selectRoleIdByUserId(Long id) {
        return getBaseMapper().selectRoleIdByUserId(id);
    }

    private void addRoleMenu(Role role) {
        List<RoleMenu> collect = Arrays.stream(role.getMenuIds())
                .map(menuId -> new RoleMenu(role.getId(), menuId))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(collect);
    }
}
