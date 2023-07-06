package com.learning.content.model.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 课程预览模型类
 * @date 2023/7/6 18:02
 **/
@Data
@ToString
public class CoursePreviewDto {
    // 课程基本信息，营销信息
    private CourseBaseInfoDto courseBase;

    // 课程计划信息
    private List<TeachPlanDto> teachplans;

    // 课程师资信息

}
