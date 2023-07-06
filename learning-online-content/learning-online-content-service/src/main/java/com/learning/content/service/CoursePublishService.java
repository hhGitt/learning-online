package com.learning.content.service;

import com.learning.content.model.dto.CoursePreviewDto;

import java.io.File;

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

    /**
     * 提交审核
     *
     * @param courseId 课程id
     */
    public void commitAudit(Long companyId, Long courseId);

    /**
     * 课程发布接口
     *
     * @param companyId 机构id
     * @param courseId  课程id
     */
    public void publish(Long companyId, Long courseId);

    /**
     * 课程静态化
     *
     * @param courseId 课程id
     * @return File 静态化文件
     */
    public File generateCourseHtml(Long courseId);

    /**
     * 上传课程静态化页面
     *
     * @param file 静态化文件
     */
    public void uploadCourseHtml(Long courseId, File file);


}
