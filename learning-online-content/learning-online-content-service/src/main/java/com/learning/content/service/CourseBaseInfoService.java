package com.learning.content.service;

import com.learning.base.model.PageParams;
import com.learning.base.model.PageResult;
import com.learning.content.model.dto.QueryCourseParamsDto;
import com.learning.content.model.po.CourseBase;

/**
 * @author HH
 * @version 1.0
 * @description 课程信息管理接口
 * @date 2023/6/29 16:08
 **/
public interface CourseBaseInfoService {
    // 课程分页查询
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto);
}
