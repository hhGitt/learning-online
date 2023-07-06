package com.learning.content.api;

import com.learning.content.model.dto.CoursePreviewDto;
import com.learning.content.service.CoursePublishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/6 19:35
 **/

@Slf4j
@RestController
@Api(value = "课程公开查询接口",tags = "课程公开查询接口")
@RequestMapping("/open")
public class CourseOpenController {

    @Autowired
    CoursePublishService coursePublishService;

    @ApiOperation(value = "根据课程id查询课程信息")
    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewDto getPreviewInfo(@PathVariable("courseId") Long courseId) {
        //获取课程预览信息
        CoursePreviewDto coursePreviewInfo = coursePublishService.getCoursePreviewInfo(courseId);
        return coursePreviewInfo;
    }

}
