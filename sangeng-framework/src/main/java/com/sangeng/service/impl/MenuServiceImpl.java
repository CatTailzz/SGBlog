package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.entity.Menu;
import com.sangeng.mapper.MenuMapper;
import com.sangeng.service.MenuService;
import com.sangeng.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-09-13 10:40:43
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<String> selectPermsByUserId(Long id) {
        //根据用户id查询权限，如果是1就是超级管理员,要求所有菜单类型为C或F，状态正常
        if(SecurityUtils.isAdmin()){
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Menu::getMenuType, SystemConstants.MENU, SystemConstants.BUTTON);
            queryWrapper.eq(Menu::getStatus, SystemConstants.STATUS_NORMAL);
            List<Menu> menuList = list(queryWrapper);
            List<String> perms = menuList.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        //否则返回该用户具有的权限信息
        return getBaseMapper().selectPermsByUserId(id);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        //判断是不是管理员
        //是 返回所有符合要求的menu
        if(SecurityUtils.isAdmin()){
            menus = menuMapper.selectAllRouterMenu();
        }else{
            //不是 返回用户具有的menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }
        //构建tree
        List<Menu> menuTree = builderMenuTree(menus, 0L);
        return menuTree;
    }

    @Override
    public List<Menu> selectMenuList(Menu menu) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<Menu>();
        queryWrapper.like(StringUtils.hasText(menu.getMenuName()),Menu::getMenuName, menu.getMenuName());
        queryWrapper.eq(StringUtils.hasText(menu.getStatus()),Menu::getStatus, menu.getStatus());
        queryWrapper.orderByAsc(Menu::getParentId, Menu::getOrderNum);
        List<Menu> menus = list(queryWrapper);
        return menus;
    }

    @Override
    public boolean hasChild(Long menuId) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getParentId, menuId);
        return count(wrapper) != 0;
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long id) {
        return getBaseMapper().selectMenuListByRoleId(id);
    }


    private List<Menu> builderMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());
        return menuTree;
    }

    /**
     * 获取传入参数的子menu集合
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m->m.setChildren(getChildren(m, menus)))
                .collect(Collectors.toList());
        return childrenList;
    }
}
