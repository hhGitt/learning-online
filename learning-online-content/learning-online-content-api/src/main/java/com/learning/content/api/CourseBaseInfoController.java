package com.learning.content.api;

import com.learning.content.model.dto.QueryCourseParamsDto;
import com.learning.content.model.po.CourseBase;
import com.learning.base.model.PageParams;
import com.learning.base.model.PageResult;
import com.learning.content.service.CourseBaseInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/6/29 11:50
 **/
@ApiOperation(value = "课程信息管理接口", tags = "课程信息管理接口")
@RestController
public class CourseBaseInfoController {

    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody(required = false) QueryCourseParamsDto queryCourseParamsDto) {
        return courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
    }
}
