package com.learning.content.api;

import com.learning.content.model.dto.CoursePreviewDto;
import com.learning.content.service.CoursePublishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/6 15:33
 **/
@Slf4j
@Controller
public class CoursePublishController {

    @Autowired
    CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{courseId}")
    public ModelAndView preview(@PathVariable("courseId") Long courseId) {
        ModelAndView modelAndView = new ModelAndView();
        // 查询课程信息
        CoursePreviewDto coursePreviewDto = coursePublishService.getCoursePreviewInfo(courseId);
        modelAndView.addObject("model",coursePreviewDto);
        modelAndView.setViewName("course_template");  // 根据视图名找到.ftl文件
        return modelAndView;
    }
}
