package com.learning.content.service.impl;

import com.learning.content.mapper.CourseCategoryMapper;
import com.learning.content.model.dto.CourseCategoryTreeDto;
import com.learning.content.model.po.CourseCategory;
import com.learning.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/6/30 17:13
 **/
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        // 调用mapper递归查询分类信息
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryMapper.selectTreeNodes("1");
        // 先将list转为map，key为id，value就是CourseCategoryTreeDto对象，filter排除根节点
        Map<String, CourseCategoryTreeDto> mapTemp = courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).collect(Collectors.toMap(CourseCategory::getId, value -> value, (key1, key2) -> key2));
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = new ArrayList<>();
        // 从头遍历courseCategoryTreeDtos，一边遍历一边查找子节点
        courseCategoryTreeDtos.stream().filter(item -> !id.equals(item.getId())).forEach(item -> {
            if (item.getParentid().equals(id)) {
                courseCategoryTreeDtoList.add(item);
            } else {
                // 找到父节点
                CourseCategoryTreeDto courseCategoryTreeDto = mapTemp.get(item.getParentid());
                if (courseCategoryTreeDto.getChildrenTreeNodes() == null) {
                    courseCategoryTreeDto.setChildrenTreeNodes(new ArrayList<>());
                }
                courseCategoryTreeDto.getChildrenTreeNodes().add(item);
            }
        });
        return courseCategoryTreeDtoList;
    }
}
