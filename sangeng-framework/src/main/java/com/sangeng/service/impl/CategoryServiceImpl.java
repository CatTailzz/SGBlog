package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.CategoryVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.mapper.CategoryMapper;
import com.sangeng.service.ArticleService;
import com.sangeng.service.CategoryService;
import com.sangeng.domain.entity.Category;
import com.sangeng.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2023-08-23 13:16:38
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private ArticleService articleService;

    @Override
    public ResponseResult getCategoryList() {
        //查询文章表，状态为已发布
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> articleList = articleService.list(articleWrapper);
        System.out.println(articleList);
        //获取文章的分类ID，去重
        Set<Long> categoryIds = articleList.stream()
                .map(a -> a.getCategoryId())
                .collect(Collectors.toSet());
        //查询分类表
        List<Category> categories = listByIds(categoryIds);
        categories = categories.stream()
                .filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
        //封装vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);
        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public List<CategoryVo> listAllCateGory() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, SystemConstants.STATUS_NORMAL);
        List<Category> list = list(wrapper);
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(list, CategoryVo.class);
        return categoryVos;
    }

    @Override
    public PageVo selectCategoryPage(Integer pageNum, Integer pageSize, Category category) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(category.getStatus()), Category::getStatus, category.getStatus());
        queryWrapper.like(StringUtils.hasText(category.getName()), Category::getName, category.getName());

        Page<Category> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, queryWrapper);

        List<Category> categories = page.getRecords();
        PageVo pageVo = new PageVo();
        pageVo.setTotal(page.getTotal());
        pageVo.setRows(categories);
        return pageVo;
    }

    @Override
    public ResponseResult add(Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper();
        wrapper.eq(StringUtils.hasText(category.getName()), Category::getName, category.getName());
        if(count(wrapper) > 0){
            return ResponseResult.errorResult(500,"新增分类失败，请检查分类名");
        }
        save(category);
        return ResponseResult.okResult();
    }
}
