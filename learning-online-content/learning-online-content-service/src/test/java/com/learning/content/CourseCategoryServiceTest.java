package com.learning.content;

import com.learning.content.model.dto.CourseCategoryTreeDto;
import com.learning.content.service.CourseCategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/6/30 17:30
 **/
@SpringBootTest
public class CourseCategoryServiceTest {

    @Autowired
    CourseCategoryService courseCategoryService;

    @Test
    public void testCourseCategoryService() {
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = courseCategoryService.queryTreeNodes("1");
        System.out.println(courseCategoryTreeDtos);
    }
}
