package com.learning.content.service.jobhandler;

import com.learning.base.execption.LearningOnlineException;
import com.learning.content.service.CoursePublishService;
import com.learning.messagesdk.model.po.MqMessage;
import com.learning.messagesdk.service.MessageProcessAbstract;
import com.learning.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author HH
 * @version 1.0
 * @description 课程发布任务类
 * @date 2023/7/8 10:51
 **/
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Autowired
    CoursePublishService coursePublishService;

    // 任务调度入口
    @XxlJob("CoursePublishJobHandler")
    public void CoursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();  // 执行器序号，从0开始
        int shardTotal = XxlJobHelper.getShardTotal();  // 执行器总数
        // 调用抽象类方法执行任务
        process(shardIndex, shardTotal, "course_publish", 30, 60);
    }

    /**
     * 执行课程发布逻辑
     *
     * @param mqMessage 执行任务内容
     * @return
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        // 从mqMessage拿到课程id
        long courseId = Long.parseLong(mqMessage.getBusinessKey1());
        // 课程静态上传minio
        generateCourseHtml(mqMessage, courseId);
        // 向elasticsearch写索引
        saveCourseIndex(mqMessage, courseId);
        // 向redis写缓存
        saveCourseCache(mqMessage, courseId);
        return true;
    }

    /**
     * 生成课程静态化页面并上传至文件系统，第一阶段
     *
     * @param mqMessage 消息信息
     * @param courseId  课程id
     */
    private void generateCourseHtml(MqMessage mqMessage, long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 任务幂等性处理
        // 查询数据取出该阶段执行状态
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0) {
            log.debug("课程静态化完成，无需处理");
            return;
        }
        // 开始进行课程静态化 html页面
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file ==null){
            LearningOnlineException.cast("生成静态页面为空");
        }
        // 将html上传minio
        coursePublishService.uploadCourseHtml(courseId, file);

        // 任务处理完成写任务状态为已完成
        mqMessageService.completedStageOne(taskId);
    }

    /**
     * 保存课程索引信息，第二阶段
     *
     * @param mqMessage 消息信息
     * @param courseId  课程id
     */
    private void saveCourseIndex(MqMessage mqMessage, long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 任务幂等性
        int stageTwo = mqMessageService.getStageTwo(taskId);
        if (stageTwo > 0) {
            log.debug("保存课程索引信息完成，无需处理");
            return;
        }
        // 查询课程学习，调用搜索服务添加索引

        // 任务处理完成写任务状态为已完成
        mqMessageService.completedStageTwo(taskId);
    }

    /**
     * 将课程信息缓存至redis,第三阶段
     *
     * @param mqMessage 消息信息
     * @param courseId  课程id
     */
    private void saveCourseCache(MqMessage mqMessage, long courseId) {
        Long taskId = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 任务幂等性
        int stageThree = mqMessageService.getStageThree(taskId);
        if (stageThree > 0) {
            log.debug("将课程信息缓存至redis完成，无需处理");
            return;
        }


        // 任务处理完成写任务状态为已完成
        mqMessageService.completedStageThree(taskId);
    }
}
