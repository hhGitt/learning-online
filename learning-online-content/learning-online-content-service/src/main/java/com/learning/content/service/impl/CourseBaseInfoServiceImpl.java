package com.learning.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.base.execption.LearningOnlineException;
import com.learning.base.model.PageParams;
import com.learning.base.model.PageResult;
import com.learning.content.mapper.*;
import com.learning.content.model.dto.AddCourseDto;
import com.learning.content.model.dto.CourseBaseInfoDto;
import com.learning.content.model.dto.EditCourseDto;
import com.learning.content.model.dto.QueryCourseParamsDto;
import com.learning.content.model.po.*;
import com.learning.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/6/29 16:10
 **/
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Autowired
    CourseTeacherMapper courseTeacherMapper;
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;


    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 根据课程名模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()), CourseBase::getName, courseParamsDto.getCourseName());
        // 根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, courseParamsDto.getAuditStatus());
        // 根据发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()), CourseBase::getStatus, courseParamsDto.getPublishStatus());
        // 分页参数对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 开始分页查询
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        List<CourseBase> items = pageResult.getRecords();
        long total = pageResult.getTotal();
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(items, total, pageResult.getPages(), pageParams.getPageSize());
        return courseBasePageResult;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        // 参数合法性校验(通过Validated实现)
        // 课程基本信息表course_base写入数据
        CourseBase courseBaseNew = new CourseBase();
        // 属性名称相同直接拷贝
        BeanUtils.copyProperties(addCourseDto, courseBaseNew);
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setCreateDate(LocalDateTime.now());
        // 审核状态默认未提交
        courseBaseNew.setAuditStatus("202002");
        // 发布状态为未发布
        courseBaseNew.setStatus("203001");
        // 插入数据库
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            throw new LearningOnlineException("添加课程失败");
        }
        // 课程营销表course_market写入数据
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarketNew);
        Long id = courseBaseNew.getId();
        courseMarketNew.setId(id);
        int courseMarketInsert = saveCourseMarket(courseMarketNew);
        if (courseMarketInsert <= 0) {
            throw new LearningOnlineException("添加课程营销信息失败");
        }
        CourseBaseInfoDto courseBaseInfo = getCourseById(id);
        return courseBaseInfo;
    }

    @Override
    public CourseBaseInfoDto getCourseById(Long courseId) {
        // 从课程基本信息表查询
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            return null;
        }
        // 从课程营销表查询
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (courseMarket == null) {
            return null;
        }
        // 组装
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        // 从课程分类表中查询分类信息
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBaseInfoDto.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBaseInfoDto.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        return courseBaseInfoDto;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        Long courseId = dto.getId();
        // 查询课程
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        // 查询营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (courseBase == null || courseMarket == null) {
            LearningOnlineException.cast("课程不存在");
        }
        // 数据合法性校验
        // 根据具体业务逻辑去校验
        // 本机构只能修改本机构课程
        if (!companyId.equals(courseBase.getCompanyId())) {
            LearningOnlineException.cast("本机构只能修改本机构的课程");
        }
        BeanUtils.copyProperties(dto, courseBase);
        BeanUtils.copyProperties(dto, courseMarket);
        // 修改时间
        courseBase.setChangeDate(LocalDateTime.now());
        // 更新课程基本信息表
        int courseBaseUpdate = courseBaseMapper.updateById(courseBase);
        if (courseBaseUpdate <= 0) {
            LearningOnlineException.cast("修改课程失败");
        }
        // 更新营销数据表
        int courseMarketUpdate = courseMarketMapper.updateById(courseMarket);
        if (courseMarketUpdate <= 0) {
            LearningOnlineException.cast("修改课程失败");
        }
        CourseBaseInfoDto courseById = getCourseById(courseId);
        return courseById;
    }

    @Transactional
    @Override
    public void removeCourseBase(Long courseId) {
        // 删除 课程基本信息、课程营销信息、课程计划、课程计划关联信息、课程师资
        // 删除课程基本信息
        courseBaseMapper.deleteById(courseId);
        // 删除课程营销信息
        courseMarketMapper.deleteById(courseId);
        // 删除课程计划
        LambdaQueryWrapper<Teachplan> teachplanLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachplanLambdaQueryWrapper.eq(Teachplan::getCourseId, courseId);
        teachplanMapper.delete(teachplanLambdaQueryWrapper);
        // 删除课程计划关联信息
        LambdaQueryWrapper<TeachplanMedia> teachplanMediaLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teachplanMediaLambdaQueryWrapper.eq(TeachplanMedia::getCourseId, courseId);
        teachplanMediaMapper.delete(teachplanMediaLambdaQueryWrapper);
        // 删除课程work管联信息(未开发)
        // 删除课程师资
        LambdaQueryWrapper<CourseTeacher> courseTeacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        courseTeacherLambdaQueryWrapper.eq(CourseTeacher::getCourseId, courseId);
        courseTeacherMapper.delete(courseTeacherLambdaQueryWrapper);
    }

    // 单独写一个方法保存营销信息，逻辑，存在则更新，不在则添加
    private int saveCourseMarket(CourseMarket courseMarketNew) {
        // 参数合法性校验
        String charge = courseMarketNew.getCharge();
        if ((StringUtils.isEmpty(charge))) {
            throw new LearningOnlineException("收费规则为空");
        }
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice().floatValue() <= 0) {
                throw new LearningOnlineException("课程价格不能为空并且必须大于0");
            }
        }
        // 数据查询营销信息，不存在则添加
        Long id = courseMarketNew.getId();
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if (courseMarket == null) {
            // 插入数据
            int insert = courseMarketMapper.insert(courseMarketNew);
            return insert;
        } else {
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            courseMarket.setId(id);
            // 更新
            int insert = courseMarketMapper.updateById(courseMarket);
            return insert;
        }
    }
}
