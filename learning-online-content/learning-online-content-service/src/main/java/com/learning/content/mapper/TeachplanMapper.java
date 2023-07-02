package com.learning.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.content.model.dto.TeachPlanDto;
import com.learning.content.model.po.Teachplan;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author HH
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {
    /**
     * 课程计划查询
     * @param courseId 课程Id
     * @return 课程计划树形数据
     */
    public List<TeachPlanDto> selectTreeNode(Long courseId);
}
