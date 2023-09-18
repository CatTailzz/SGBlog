package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Role;
import com.sangeng.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/18 10:04
 **/
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, Role role){
        return roleService.selectRolePage(pageNum, pageSize, role);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody Role role){
        roleService.updateById(role);
        return ResponseResult.okResult();
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody Role role){
        roleService.addRole(role);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult getinfo(@PathVariable Long id){
        Role role = roleService.getById(id);
        return ResponseResult.okResult(role);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody Role role){
        roleService.updateRole(role);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable Long id){
        roleService.removeById(id);
        return ResponseResult.okResult();
    }

}
