package com.sangeng.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.dto.ArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.vo.ArticleVo;
import com.sangeng.domain.vo.PageVo;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/8/16 17:01
 **/
public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult add(AddArticleDto article);

    PageVo selectArticlePage(Integer pageNum, Integer pageSize, Article article);

    ArticleVo getInfo(Long id);

    void edit(ArticleDto article);

}
