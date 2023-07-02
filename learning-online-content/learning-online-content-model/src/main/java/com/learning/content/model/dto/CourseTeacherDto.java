package com.learning.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author HH
 * @version 1.0
 * @description 课程老师Dto
 * @date 2023/7/2 12:08
 **/
@Data
@ToString
public class CourseTeacherDto {
    @NotEmpty(message = "课程id不能为空")
    @ApiModelProperty(value = "课程id",required = true)
    private Long courseId;

    @ApiModelProperty(value = "教师姓名")
    private String teacherName;

    @ApiModelProperty(value = "教师职位")
    private String position;

    @ApiModelProperty(value = "教师简介")
    private String introduction;
}
