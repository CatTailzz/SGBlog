package com.sangeng.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/14 20:09
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditTagDto {
    private Long id;
    private String name;
    private String remark;
}
