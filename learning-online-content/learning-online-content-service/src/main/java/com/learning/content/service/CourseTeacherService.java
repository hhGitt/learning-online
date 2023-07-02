package com.learning.content.service;

import com.learning.content.model.dto.CourseTeacherDto;
import com.learning.content.model.dto.EditCourseTeacherDto;
import com.learning.content.model.po.CourseTeacher;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 课程老师相关接口
 * @date 2023/7/2 11:52
 **/
public interface CourseTeacherService {

    /**
     * 查询课程教师信息列表
     *
     * @param courseId 课程id
     * @return 课程教师信息列表
     */
    public List<CourseTeacher> queryCourseTeacherList(Long courseId);

    /**
     * 添加课程教师信息
     *
     * @param courseTeacherDto 添加信息
     * @return 课程教师详细信息
     */
    public CourseTeacher createCourseTeacher(CourseTeacherDto courseTeacherDto);

    /**
     * 更新课程教师信息
     *
     * @param editCourseTeacherDto 更细信息
     * @return 课程教师详细信息
     */
    public CourseTeacher updateCourseTeacher(EditCourseTeacherDto editCourseTeacherDto);

    /**
     * 根据课程Id和course_teacher的id删除该条教师信息
     * @param courseId 课程Id
     * @param courseTeacherId 表id
     */
    public void removeCourseTeacher(Long courseId, Long courseTeacherId);
}
