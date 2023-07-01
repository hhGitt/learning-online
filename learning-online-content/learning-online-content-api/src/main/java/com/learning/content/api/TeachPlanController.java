package com.learning.content.api;

import com.learning.content.model.dto.SaveTeachPlanDto;
import com.learning.content.model.dto.TeachPlanDto;
import com.learning.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description 课程计划相关接口
 * @date 2023/7/1 14:58
 **/
@Slf4j
@RestController
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
public class TeachPlanController {

    @Autowired
    TeachPlanService teachPlanService;

    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId) {
        List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
        return teachPlanTree;
    }

    @ApiOperation("修改或添加课程计划")
    @PostMapping("/teachplan")
    public void saveTeachPlan(@RequestBody SaveTeachPlanDto saveTeachPlanDto) {
        teachPlanService.saveTeachPlan(saveTeachPlanDto);
    }
}
