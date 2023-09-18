package com.sangeng.domain.vo;

import com.sangeng.domain.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/13 13:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutersVo {
    private List<Menu> menus;
}
