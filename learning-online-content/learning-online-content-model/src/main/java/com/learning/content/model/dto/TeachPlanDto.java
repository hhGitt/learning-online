package com.learning.content.model.dto;

import com.learning.content.model.po.Teachplan;
import com.learning.content.model.po.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 课程计划信息模型类
 * @date 2023/7/1 14:55
 **/
@Data
@ToString
public class TeachPlanDto extends Teachplan {
    // 与媒体资源管理的信息
    private TeachplanMedia teachplanMedia;
    // 小章节list
    private List<TeachPlanDto> teachPlanTreeNodes;
}
