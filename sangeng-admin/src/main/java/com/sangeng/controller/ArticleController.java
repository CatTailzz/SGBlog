package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.AddArticleDto;
import com.sangeng.domain.dto.ArticleDto;
import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.ArticleVo;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/15 14:06
 **/
@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseResult add(@RequestBody AddArticleDto article){
        return articleService.add(article);
    }

    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, Article article){
        PageVo pageVo = articleService.selectArticlePage(pageNum, pageSize, article);
        return ResponseResult.okResult(pageVo);
    }

    @GetMapping("/{id}")
    public ResponseResult getInfo(@PathVariable Long id){
        ArticleVo article = articleService.getInfo(id);
        return ResponseResult.okResult(article);
    }

    @PutMapping
    public ResponseResult edit(@RequestBody ArticleDto article){
        articleService.edit(article);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteById(@PathVariable Long id){
        articleService.removeById(id);
        return ResponseResult.okResult();
    }
}
