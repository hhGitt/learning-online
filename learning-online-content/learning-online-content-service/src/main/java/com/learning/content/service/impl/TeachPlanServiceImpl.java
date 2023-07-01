package com.learning.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.learning.content.mapper.TeachplanMapper;
import com.learning.content.model.dto.SaveTeachPlanDto;
import com.learning.content.model.dto.TeachPlanDto;
import com.learning.content.model.po.Teachplan;
import com.learning.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private int getTeachPlanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId).eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }
}
