package com.learning.content.service.impl;

import com.learning.content.model.dto.CourseBaseInfoDto;
import com.learning.content.model.dto.CoursePreviewDto;
import com.learning.content.model.dto.TeachPlanDto;
import com.learning.content.service.CourseBaseInfoService;
import com.learning.content.service.CoursePublishService;
import com.learning.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 课程发布相关接口实现
 * @date 2023/7/6 18:08
 **/
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    TeachPlanService teachPlanService;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        // 课程基本信息，营销信息
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseById(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        // 课程计划信息
        List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
        coursePreviewDto.setTeachplans(teachPlanTree);
        return coursePreviewDto;
    }
}
