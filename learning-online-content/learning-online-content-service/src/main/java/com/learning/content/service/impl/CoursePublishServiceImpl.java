package com.learning.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.learning.base.execption.CommonError;
import com.learning.base.execption.LearningOnlineException;
import com.learning.base.utils.StringUtil;
import com.learning.content.config.MultipartSupportConfig;
import com.learning.content.feginclient.MediaServiceClient;
import com.learning.content.mapper.CourseBaseMapper;
import com.learning.content.mapper.CourseMarketMapper;
import com.learning.content.mapper.CoursePublishMapper;
import com.learning.content.mapper.CoursePublishPreMapper;
import com.learning.content.model.dto.CourseBaseInfoDto;
import com.learning.content.model.dto.CoursePreviewDto;
import com.learning.content.model.dto.TeachPlanDto;
import com.learning.content.model.po.*;
import com.learning.content.service.CourseBaseInfoService;
import com.learning.content.service.CoursePublishService;
import com.learning.content.service.CourseTeacherService;
import com.learning.content.service.TeachPlanService;
import com.learning.messagesdk.mapper.MqMessageMapper;
import com.learning.messagesdk.model.po.MqMessage;
import com.learning.messagesdk.service.MqMessageService;
import freemarker.core.ParseException;
import freemarker.template.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.learning.content.model.dto.CourseBaseInfoDto;
import com.learning.content.model.dto.CoursePreviewDto;
import com.learning.content.model.dto.TeachPlanDto;
import com.learning.content.service.CourseBaseInfoService;
import com.learning.content.service.CoursePublishService;
import com.learning.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 课程发布相关接口实现
 * @date 2023/7/6 18:08
 **/
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    TeachPlanService teachPlanService;
    @Autowired
    CourseTeacherService courseTeacherService;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CoursePublishMapper coursePublishMapper;
    @Autowired
    MqMessageService mqMessageService;
    @Autowired
    MediaServiceClient mediaServiceClient;

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        // 课程基本信息，营销信息
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseById(courseId);
        coursePreviewDto.setCourseBase(courseBaseInfoDto);
        // 课程计划信息
        List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
        coursePreviewDto.setTeachplans(teachPlanTree);
        // 课程师资信息
        List<CourseTeacher> courseTeachers = courseTeacherService.queryCourseTeacherList(courseId);
        coursePreviewDto.setCourseTeachers(courseTeachers);
        return coursePreviewDto;
    }

    @Transactional
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        CourseBaseInfoDto courseBaseInfoDto = courseBaseInfoService.getCourseById(courseId);
        if (courseBaseInfoDto == null) {
            LearningOnlineException.cast("课程找不到");
        }
        // 机构判断
        if (companyId == null || !companyId.equals(courseBaseInfoDto.getCompanyId())) {
            LearningOnlineException.cast("机构错误");
        }
        // 审核状态
        String auditStatus = courseBaseInfoDto.getAuditStatus();
        // 如果课程的审核状态为已提交则不允许提交
        if (auditStatus.equals("202003")) {
            LearningOnlineException.cast("课程已提交请等待审核");
        }
        // 课程的图片，计划信息没有填写不允许提交
        String pic = courseBaseInfoDto.getPic();
        if (StringUtil.isEmpty(pic)) {
            LearningOnlineException.cast("请上传课程图片");
        }
        // 课程计划
        List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
        if (teachPlanTree == null || teachPlanTree.isEmpty()) {
            LearningOnlineException.cast("请编写课程计划");
        }
        // 查询到课程基本信息，营销信息，计划等信息插入到课程预发布表
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseBaseInfoDto, coursePublishPre);
        // 营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        // 转json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);
        // 计划信息
        String teachPlanString = JSON.toJSONString(teachPlanTree);
        coursePublishPre.setTeachplan(teachPlanString);
        // 教师信息
        List<CourseTeacher> courseTeachers = courseTeacherService.queryCourseTeacherList(courseId);
        String courseTeachersString = JSON.toJSONString(courseTeachers);
        coursePublishPre.setTeachers(courseTeachersString);
        // 状态
        coursePublishPre.setStatus("202003");
        // 提交时间
        coursePublishPre.setCreateDate(LocalDateTime.now());
        // 查询预发布表，没有则插入
        CoursePublishPre coursePublishPreObj = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreObj == null) {
            // 插入
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            // 更新
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        // 更新到课程基本信息表的审核状态为已提交
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setAuditStatus("202003");  // 审核状态为已提交
        courseBaseMapper.updateById(courseBase);
    }

    @Transactional
    @Override
    public void publish(Long companyId, Long courseId) {
        // 查询预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (companyId == null || !companyId.equals(coursePublishPre.getCompanyId())) {
            LearningOnlineException.cast("机构错误");
        }
        // 状态
        String status = coursePublishPre.getStatus();
        if (!"202004".equals(status)) {
            LearningOnlineException.cast("课程未审核通过，不允许发布");
        }
        // 向课程发布表写入数据
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        CoursePublish coursePublishObj = coursePublishMapper.selectById(courseId);
        if (coursePublishObj == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        // 向消息表写入数据
        saveCoursePublishMessage(courseId);
        // 将预发布表数据删除
        coursePublishPreMapper.deleteById(courseId);
    }

    @Override
    public File generateCourseHtml(Long courseId) {
        Configuration configuration = new Configuration(Configuration.getVersion());
        File tempFile = null;
        try {
            // 拿到classpath路径
            String path = this.getClass().getResource("/").getPath();
            // 指定模板目录
            configuration.setDirectoryForTemplateLoading(new File(path + "/templates/"));
            // 指定编码
            configuration.setDefaultEncoding("utf-8");
            // 得到模板
            Template template = configuration.getTemplate("course_template.ftl");
            // 数据信息
            CoursePreviewDto coursePreviewDto = this.getCoursePreviewInfo(courseId);
            HashMap<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewDto);
            // 模板，数据
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            // 输入流
            InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
            tempFile = File.createTempFile("coursepublish", ".html");
            // 输出流
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            // 使用流将html写入文件
            IOUtils.copy(inputStream, fileOutputStream);
        } catch (Exception e) {
            log.error("页面静态化出现问题，课程id:{}", courseId, e);
            e.printStackTrace();
        }
        return tempFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        try {
            // 将file转为MultipartFile
            MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
            String s = mediaServiceClient.uploadFile(multipartFile, "course/" + courseId + ".html");
            if (s == null) {
                log.debug("远程降级，上传为null,课程id:{}", courseId);
                LearningOnlineException.cast("上传静态文件存在异常");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LearningOnlineException.cast("上传静态文件存在异常");
        }
    }

    /**
     * 保存消息表记录
     *
     * @param courseId 课程id
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            LearningOnlineException.cast(CommonError.UNKOWN_ERROR);
        }
    }
}
