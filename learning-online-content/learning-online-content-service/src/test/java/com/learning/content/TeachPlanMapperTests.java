package com.learning.content;

import com.learning.content.mapper.CourseCategoryMapper;
import com.learning.content.mapper.TeachplanMapper;
import com.learning.content.model.dto.CourseCategoryTreeDto;
import com.learning.content.model.dto.TeachPlanDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/6/30 17:06
 **/
@SpringBootTest
public class TeachPlanMapperTests {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Test
    public void testTeachPlanMapper() {
        List<TeachPlanDto> teachPlanDtos = teachplanMapper.selectTreeNode(117L);
        System.out.println(teachPlanDtos);
    }
}
