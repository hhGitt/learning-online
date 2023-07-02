package com.learning.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/1 10:39
 **/
@Data
public class EditCourseDto extends AddCourseDto {
    @ApiModelProperty(value = "课程id", required = true)
    private Long id;
}
