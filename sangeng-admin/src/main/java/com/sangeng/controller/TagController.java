package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.domain.dto.EditTagDto;
import com.sangeng.domain.dto.TagListDto;
import com.sangeng.domain.entity.Tag;
import com.sangeng.domain.vo.PageVo;
import com.sangeng.domain.vo.TagVo;
import com.sangeng.service.TagService;
import com.sangeng.utils.BeanCopyUtils;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zsj
 * @Description :
 * @Create on : 2023/9/12 16:18
 **/
@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, TagListDto tagListDto){
        return tagService.pageTagList(pageNum, pageSize, tagListDto);
    }

    @PostMapping
    public ResponseResult addTag(@RequestBody TagListDto tagListDto){
        Tag tag = BeanCopyUtils.copyBean(tagListDto, Tag.class);
        tagService.save(tag);
        return ResponseResult.okResult();
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteTag(@PathVariable Long id){
        tagService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult getTagById(@PathVariable Long id){
        Tag tag = tagService.getById(id);
        return ResponseResult.okResult(tag);
    }

    @PutMapping
    public ResponseResult updateTag(@RequestBody EditTagDto tagDto){
        Tag tag = BeanCopyUtils.copyBean(tagDto, Tag.class);
        tagService.updateById(tag);
        return ResponseResult.okResult();
    }

    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        List<TagVo> list = tagService.listAllTag();
        return ResponseResult.okResult(list);
    }
}
