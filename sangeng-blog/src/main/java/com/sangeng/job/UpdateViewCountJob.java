package com.sangeng.job;

import com.sangeng.domain.entity.Article;
import com.sangeng.service.ArticleService;
import com.sangeng.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/11 14:56
 **/
@Component
public class UpdateViewCountJob {
    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void updateViewCount(){
        //获取浏览量
        Map<String, Object> viewContMap = redisCache.getCacheMap("article:viewCount");
        List<Article> articles = viewContMap.entrySet()
                .stream()
                .map(entry -> new Article(Long.valueOf(entry.getKey()), Long.valueOf(entry.getValue().toString())))
                .collect(Collectors.toList());
        //更新到数据库
        articleService.updateBatchById(articles);
    }
}
