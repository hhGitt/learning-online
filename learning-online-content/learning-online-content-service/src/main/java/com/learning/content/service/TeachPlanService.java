package com.learning.content.service;

import com.learning.content.model.dto.BindTeachPlanMediaDto;
import com.learning.content.model.dto.SaveTeachPlanDto;
import com.learning.content.model.dto.TeachPlanDto;
import com.learning.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 课程计划管理相关接口
 * @date 2023/7/1 15:49
 **/
public interface TeachPlanService {
    /**
     * 根据课程id查询课程
     *
     * @param courseId 课程id
     * @return 树形课程计划数据
     */
    public List<TeachPlanDto> findTeachPlanTree(Long courseId);

    /**
     * 新增/修改/保存课程计划
     *
     * @param saveTeachPlanDto 课程计划
     */
    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto);

    /**
     * 根据教学计划id删除
     *
     * @param teachPlanId 教学计划id
     */
    public void removeTeachPlan(Long teachPlanId);

    /**
     * 移动教学计划
     *
     * @param step        移动步长（负数向上，正数向下）
     * @param teachPlanId 教学计划id
     */
    public void moveTeachPlan(int step, Long teachPlanId);

    /**
     * 教学计划绑定媒资
     *
     * @param bindTeachPlanMediaDto 绑定相关信息
     * @return TeachplanMedia
     */
    public TeachplanMedia associationMedia(BindTeachPlanMediaDto bindTeachPlanMediaDto);

}
