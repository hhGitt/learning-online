package com.learning.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.base.execption.LearningOnlineException;
import com.learning.content.mapper.CourseBaseMapper;
import com.learning.content.mapper.CourseTeacherMapper;
import com.learning.content.model.po.CourseBase;
import com.learning.content.model.po.CourseTeacher;
import com.learning.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/2 11:53
 **/
@Slf4j
@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Override
    public List<CourseTeacher> queryCourseTeacherList(Long courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId);
        List<CourseTeacher> courseTeachers = courseTeacherMapper.selectList(queryWrapper);
        return courseTeachers;
    }

    @Override
    public CourseTeacher createCourseTeacher(CourseTeacher courseTeacherDto) {
        // 查询是否存在课程
        CourseBase courseBase = courseBaseMapper.selectById(courseTeacherDto.getCourseId());
        if (courseBase == null) {
            throw new LearningOnlineException("该课程不存在");
        }
        // 查询该教师消息是否存在
        CourseTeacher courseTeacher = courseTeacherMapper.selectById(courseTeacherDto.getId());
        if (courseTeacher == null) {
            courseTeacher = new CourseTeacher();
            BeanUtils.copyProperties(courseTeacherDto, courseTeacher);
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert <= 0) {
                throw new LearningOnlineException("添加教师信息失败");
            }
        } else {
            BeanUtils.copyProperties(courseTeacherDto, courseTeacher);
            courseTeacher.setCreateDate(LocalDateTime.now());
            int updateById = courseTeacherMapper.updateById(courseTeacher);
            if (updateById <= 0) {
                throw new LearningOnlineException("更新教师信息失败");
            }
        }
        return courseTeacher;
    }

    @Override
    public void removeCourseTeacher(Long courseId, Long courseTeacherId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getId, courseTeacherId);
        courseTeacherMapper.delete(queryWrapper);
    }
}
