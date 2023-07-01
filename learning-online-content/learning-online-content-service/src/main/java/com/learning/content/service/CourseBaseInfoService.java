package com.learning.content.service;

import com.learning.base.model.PageParams;
import com.learning.base.model.PageResult;
import com.learning.content.model.dto.AddCourseDto;
import com.learning.content.model.dto.CourseBaseInfoDto;
import com.learning.content.model.dto.EditCourseDto;
import com.learning.content.model.dto.QueryCourseParamsDto;
import com.learning.content.model.po.CourseBase;

/**
 * @author HH
 * @version 1.0
 * @description 课程信息管理接口
 * @date 2023/6/29 16:08
 **/
public interface CourseBaseInfoService {
    /**
     * 课程分页查询
     *
     * @param pageParams      分页查询参数
     * @param courseParamsDto 查询条件
     * @return 查询结果
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto);

    /**
     * 新增课程
     *
     * @param companyId    机构Id
     * @param addCourseDto 课程信息
     * @return 课程详细信息
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程id查询课程信息
     * @param courseId 课程id
     * @return 课程信息
     */
    CourseBaseInfoDto getCourseById(Long courseId);

    /**
     * 修改课程信息
     * @param companyId 公司id
     * @param dto 修改课程信息
     * @return 课程详细信息
     */
    CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto);
}
