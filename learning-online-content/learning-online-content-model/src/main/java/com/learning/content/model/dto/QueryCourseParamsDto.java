package com.learning.content.model.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author HH
 * @version 1.0
 * @description 课程查询参数Dto
 * @date 2023/6/29 11:42
 **/
@Data
@ToString
public class QueryCourseParamsDto {
    // 审核状态
    private String auditStatus;
    // 课程名称
    private String courseName;
    // 发布状态
    private String publishStatus;
}
