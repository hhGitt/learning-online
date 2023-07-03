package com.learning.content.api;

import com.learning.content.model.po.CourseTeacher;
import com.learning.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/2 11:46
 **/
@Slf4j
@RestController
@Api(value = "课程老师相关接口", tags = "课程老师相关接口")
public class CourseTeacherController {

    @Autowired
    CourseTeacherService courseTeacherService;

    @ApiOperation(value = "查询课程对应老师信息")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> list(@PathVariable Long courseId) {
        return courseTeacherService.queryCourseTeacherList(courseId);
    }

    @ApiOperation(value = "添加或修改课程对应老师信息")
    @PostMapping("/courseTeacher")
    public CourseTeacher saveCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        return courseTeacherService.createCourseTeacher(courseTeacher);
    }

    @ApiOperation(value = "删除课程对应老师信息")
    @DeleteMapping("/courseTeacher/course/{courseId}/{courseTeacherId}")
    public void deleteCourseTeacher(@PathVariable Long courseId, @PathVariable Long courseTeacherId) {
        courseTeacherService.removeCourseTeacher(courseId, courseTeacherId);
    }
}
