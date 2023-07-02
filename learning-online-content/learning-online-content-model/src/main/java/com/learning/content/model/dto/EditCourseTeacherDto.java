package com.learning.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/2 12:24
 **/
@Data
@ToString
public class EditCourseTeacherDto extends CourseTeacherDto {
    @NotEmpty(message = "教师id不能为空")
    @ApiModelProperty(value = "教师id", required = true)
    private Long id;

    @ApiModelProperty(value = "照片信息")
    private String photograph;

    @ApiModelProperty(value = "创建时间")
    private String createDate;
}
