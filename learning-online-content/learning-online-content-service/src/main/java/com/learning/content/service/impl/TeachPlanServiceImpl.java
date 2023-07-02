package com.learning.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.base.execption.LearningOnlineException;
import com.learning.content.mapper.TeachplanMapper;
import com.learning.content.mapper.TeachplanMediaMapper;
import com.learning.content.model.dto.SaveTeachPlanDto;
import com.learning.content.model.dto.TeachPlanDto;
import com.learning.content.model.po.Teachplan;
import com.learning.content.model.po.TeachplanMedia;
import com.learning.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/1 15:51
 **/
@Slf4j
@Service
public class TeachPlanServiceImpl implements TeachPlanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachPlanDto> findTeachPlanTree(Long courseId) {
        List<TeachPlanDto> teachPlanTree = teachplanMapper.selectTreeNode(117L);
        return teachPlanTree;
    }

    @Override
    public void saveTeachPlan(SaveTeachPlanDto saveTeachPlanDto) {
        // 确定排序字段
        Long parentId = saveTeachPlanDto.getParentid() == null ? 0L : saveTeachPlanDto.getParentid();
        Long courseId = saveTeachPlanDto.getCourseId();
        // 通过课程计划id判断是新增还是修改
        Long teachPlanId = saveTeachPlanDto.getId();
        if (teachPlanId == null) {
            // 新增
            Teachplan teachplan = new Teachplan();
            int teachPlanCount = getTeachPlanCount(courseId, parentId);
            teachplan.setOrderby(teachPlanCount + 1);
            BeanUtils.copyProperties(saveTeachPlanDto, teachplan);
            teachplanMapper.insert(teachplan);
        } else {
            // 修改
            Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
            BeanUtils.copyProperties(saveTeachPlanDto, teachplan);
            // 确定排序字段
            int teachPlanCount = getTeachPlanCount(courseId, parentId);
            teachplan.setOrderby(teachPlanCount + 1);
            teachplanMapper.updateById(teachplan);
        }
    }

    @Transactional
    @Override
    public void removeTeachPlan(Long teachPlanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachPlanId);
        // 查询是否为父节点，且是否有存在的子节点
        int childrenCount = getTeachPlanCount(teachplan.getCourseId(), teachplan.getId());
        if (childrenCount != 0) {
            throw new LearningOnlineException("课程计划信息还有子级信息，无法操作");
        }
        // 删除课程计划表对应teachPlanId计划
        teachplanMapper.deleteById(teachPlanId);
        // 删除teachPlanId对应媒体
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
        teachplanMediaMapper.delete(queryWrapper);
        // 删除teachPlanId对应work(暂未使用)
    }

    @Transactional
    @Override
    public void moveTeachPlan(int step, Long teachPlanId) {
        // 获取当前计划
        Teachplan teachPlan = teachplanMapper.selectById(teachPlanId);
        // 通过父节点找到该子节点的数量
        int childrenCount = getTeachPlanCount(teachPlan.getCourseId(), teachPlan.getId());
        // 如果计划在首尾则不用改变
        if (childrenCount == teachPlan.getOrderby() || teachPlan.getOrderby() == 1) return;
        // 查找和当前计划要交换的计划
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getOrderby, teachPlan.getOrderby() + step).eq(Teachplan::getCourseId, teachPlan.getCourseId()).eq(Teachplan::getParentid, teachPlan.getParentid());
        Teachplan moveTeachPlan = teachplanMapper.selectOne(queryWrapper);
        // 交换位置
        moveTeachPlan.setOrderby(moveTeachPlan.getOrderby() - step);
        teachPlan.setOrderby(teachPlan.getOrderby() + step);
        teachplanMapper.updateById(moveTeachPlan);
        teachplanMapper.updateById(teachPlan);
    }

    /**
     * 获取对应章节的计划数量
     *
     * @param courseId 课程id
     * @param parentId 父节点id
     * @return 计划数量
     */
    private int getTeachPlanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
