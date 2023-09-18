package com.sangeng.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.sangeng.constants.SystemConstants;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.dto.ArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.entity.ArticleTag;
import com.sangeng.domain.entity.Category;
import com.sangeng.domain.vo.*;
import com.sangeng.mapper.ArticleMapper;
import com.sangeng.service.ArticleService;
import com.sangeng.service.ArticleTagService;
import com.sangeng.service.CategoryService;
import com.sangeng.utils.BeanCopyUtils;
import com.sangeng.utils.RedisCache;
import lombok.val;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mapping.TargetAwareIdentifierAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/8/16 17:02
 **/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleTagService articleTagService;

    @Override
    public ResponseResult hotArticleList() {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Article::getViewCount);
        Page<Article> page = new Page(1, 10);
        page(page,queryWrapper);
        List<Article> articles = page.getRecords();
        //bean copy
//        List<HotArticleVo> articleVos = new ArrayList<>();
//        for(Article article : articles){
//            HotArticleVo vo = new HotArticleVo();
//            BeanUtils.copyProperties(article, vo);
//            articleVos.add(vo);
//        }
        List<HotArticleVo> vs = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);

        return ResponseResult.okResult(vs);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 1.如果有categoryId就要带上，没有就无所谓
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0,Article::getCategoryId,categoryId);
        // 2.只能是正式的
        lambdaQueryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        // 3.按照isTop降序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        //分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, lambdaQueryWrapper);

        //查询categoryname
        List<Article> articles = page.getRecords();
        //用id查name
//        for(Article article : articles){
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }
        articles.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());


        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVo.class);

        PageVo pageVo = new PageVo(articleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        //从redis中获取viewcount
        Integer viewCount = redisCache.getCacheMapValue("article:viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成vo
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        //根据分类id查询分类名称
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if(category != null){
            articleDetailVo.setCategoryName(category.getName());
        }


        //封装
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中对应id的浏览量
        redisCache.incrementCacheMapValue("article:viewCount", id.toString(), 1);
        return null;
    }

    @Override
    @Transactional
    public ResponseResult add(AddArticleDto articleDto) {
        //添加博客
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        save(article);
        //添加博客后，会得到博客的id，再将博客id和tagid关联起来，存入数据库中
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(TagId -> new ArticleTag(article.getId(), TagId))
                .collect(Collectors.toList());

        //添加博客和标签的关联
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public PageVo selectArticlePage(Integer pageNum, Integer pageSize, Article article) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(article.getTitle()), Article::getTitle, article.getTitle());
        queryWrapper.like(StringUtils.hasText(article.getSummary()), Article::getSummary, article.getSummary());

        Page<Article> page = new Page<>();

        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, queryWrapper);

        PageVo pageVo = new PageVo();
        pageVo.setRows(page.getRecords());
        pageVo.setTotal(page.getTotal());
        return  pageVo;
    }

    @Override
    public ArticleVo getInfo(Long id) {
        Article article = getById(id);
        //补充tag
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, article.getId());
        List<ArticleTag> articleTags = articleTagService.list(wrapper);
        List<Long> tags = articleTags.stream()
                .map(articleTag -> articleTag.getTagId())
                .collect(Collectors.toList());

        ArticleVo articleVo = BeanCopyUtils.copyBean(article, ArticleVo.class);
        articleVo.setTags(tags);
        return articleVo;
    }

    @Override
    public void edit(ArticleDto articleDto) {
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        updateById(article);
        //删除原来的tag和article标签关联，添加新的
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, article.getId());
        articleTagService.remove(wrapper);

        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(articleDto.getId(), tagId))
                .collect(Collectors.toList());
        articleTagService.saveBatch(articleTags);
    }
}
