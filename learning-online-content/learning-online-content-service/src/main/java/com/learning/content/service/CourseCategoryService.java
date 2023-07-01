package com.learning.content.service;

import com.learning.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/6/30 17:11
 **/
public interface CourseCategoryService {
    /**
     * 课程分类树形结构查询
     * @param id 查询id
     * @return 数据列表
     */
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
