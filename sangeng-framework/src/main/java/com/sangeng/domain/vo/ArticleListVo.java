package com.sangeng.domain.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/8/23 16:55
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleListVo {
    @TableId
    private Long id;
    //标题
    private String title;

    //文章摘要
    private String summary;
    //所属分类名
    private String categoryName;
    //缩略图
    private String thumbnail;

    //访问量
    private Long viewCount;


    private Date createTime;

}
