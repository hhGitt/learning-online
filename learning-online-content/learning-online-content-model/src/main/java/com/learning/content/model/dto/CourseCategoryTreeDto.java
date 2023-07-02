package com.learning.content.model.dto;

import com.learning.content.model.po.CourseCategory;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 课程分类Dto
 * @date 2023/6/30 16:27
 **/
@Data
@ToString
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    // 子节点
    private List<CourseCategoryTreeDto> childrenTreeNodes;
}
