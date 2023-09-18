package com.sangeng.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/13 21:42
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagListDto {
    private String name;
    private String remark;

}
