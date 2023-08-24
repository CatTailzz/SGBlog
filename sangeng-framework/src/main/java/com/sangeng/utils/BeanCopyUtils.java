package com.sangeng.utils;

import com.fasterxml.jackson.databind.util.BeanUtil;
import com.sangeng.domain.entity.Article;
import org.springframework.beans.BeanUtils;

import com.sangeng.domain.entity.Article;
import com.sangeng.domain.vo.HotArticleVo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/8/16 21:39
 **/
public class BeanCopyUtils {
    private BeanCopyUtils(){

    }

    public static <V> V copyBean(Object source,Class<V> clazz) {
        //创建目标对象
        V result;
        try {
            result = clazz.newInstance();
            //实现属性拷贝
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static <O,V> List<V> copyBeanList(List<O> list, Class<V> clazz){
        return list.stream()
                .map(o->copyBean(o,clazz))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("ss");

        HotArticleVo hotArticleVo = copyBean(article, HotArticleVo.class);
        System.out.println(hotArticleVo);
    }
}
