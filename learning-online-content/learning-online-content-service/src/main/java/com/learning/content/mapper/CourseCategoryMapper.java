package com.learning.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.content.model.dto.CourseCategoryTreeDto;
import com.learning.content.model.po.CourseCategory;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author HH
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {
    // 使用递归查询分类
    public List<CourseCategoryTreeDto> selectTreeNodes(String id);
}
