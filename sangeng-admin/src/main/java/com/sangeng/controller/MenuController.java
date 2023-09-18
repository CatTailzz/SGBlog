package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Menu;
import com.sangeng.domain.vo.MenuTreeVo;
import com.sangeng.domain.vo.MenuVo;
import com.sangeng.domain.vo.RoleMenuTreeSelectVo;
import com.sangeng.service.MenuService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.SystemConverter;
import jdk.nashorn.internal.ir.ReturnNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/16 16:49
 **/
@RestController
@RequestMapping("/system/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult list(Menu menu){
        List<Menu> menus = menuService.selectMenuList(menu);
        List<MenuVo> menuVos = BeanCopyUtils.copyBeanList(menus, MenuVo.class);
        return ResponseResult.okResult(menuVos);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Menu menu){
        menuService.save(menu);
        return ResponseResult.okResult();
    }

    @GetMapping("/{menuId}")
    public ResponseResult getInfo(@PathVariable Long menuId){
        Menu menu = menuService.getById(menuId);
        return ResponseResult.okResult(menu);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody Menu menu){
        if(menu.getId().equals(menu.getParentId())){
            return ResponseResult.errorResult(500,"修改菜单" + menu.getMenuName() + "失败，上级菜单不能选择自己");
        }
        menuService.updateById(menu);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{menuId}")
    public ResponseResult delete(@PathVariable Long menuId){
        if(menuService.hasChild(menuId)){
            return ResponseResult.errorResult(500, "存在子菜单不允许删除");
        }
        menuService.removeById(menuId);
        return ResponseResult.okResult();
    }

    /**
     * 获取全部菜单树
     */
    @GetMapping("/treeselect")
    public ResponseResult treeselect(){
        List<Menu> menus = menuService.selectMenuList(new Menu());
        List<MenuTreeVo> menuTreeVos = SystemConverter.buildMenuSelectTree(menus);
        return ResponseResult.okResult(menuTreeVos);
    }

    /**
     * 获取该用户的菜单树
     * 返回 全部菜单树 + 该用户的菜单id 即可
     * 前端会处理表现形式
     */
    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTreeselect(@PathVariable Long id){
        List<Menu> menus = menuService.selectMenuList(new Menu());
        List<Long> checkedKeys = menuService.selectMenuListByRoleId(id);
        List<MenuTreeVo> menuTreeVos = SystemConverter.buildMenuSelectTree(menus);
        RoleMenuTreeSelectVo vo = new RoleMenuTreeSelectVo(checkedKeys,menuTreeVos);
        return ResponseResult.okResult(vo);
    }
}
