package com.learning.content.service;

import com.learning.content.model.dto.CoursePreviewDto;

/**
 * @author HH
 * @version 1.0
 * @description 课程发布相关接口
 * @date 2023/7/6 18:07
 **/
public interface CoursePublishService {
    /**
     * 获取课程预览信息
     *
     * @param courseId 课程id
     * @return CoursePreviewDto
     */
    public CoursePreviewDto getCoursePreviewInfo(Long courseId);

}
