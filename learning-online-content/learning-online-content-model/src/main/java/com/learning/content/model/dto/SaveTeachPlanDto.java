package com.learning.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author HH
 * @version 1.0
 * @description 新增大章节，小章节，修改章节信息
 * @date 2023/7/1 16:09
 **/
@Data
@ToString
public class SaveTeachPlanDto {

    @ApiModelProperty(value = "教学计划id", required = true)
    private Long id;


    @ApiModelProperty(value = "课程计划名称", required = true)
    private String pname;

    @NotEmpty(message = "父级Id不能为空")
    @ApiModelProperty(value = "课程计划父级Id", required = true)
    private Long parentid;

    @NotEmpty(message = "层级不能为空")
    @ApiModelProperty(value = "层级，分为1、2、3级", required = true)
    private Integer grade;

    @ApiModelProperty(value = "课程类型:1视频、2文档", required = true)
    private String mediaType;

    @ApiModelProperty(value = "课程标识")
    private Long courseId;

    @ApiModelProperty(value = "课程发布标识")
    private Long coursePubId;

    @ApiModelProperty(value = "是否支持试学或预览（试看）")
    private String isPreview;

}
