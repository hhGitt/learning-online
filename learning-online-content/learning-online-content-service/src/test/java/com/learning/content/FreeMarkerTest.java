package com.learning.content;

import com.learning.content.model.dto.CoursePreviewDto;
import com.learning.content.service.CoursePublishService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * @author HH
 * @version 1.0
 * @description FreeMarkerTest
 * @date 2023/7/8 11:30
 **/
@SpringBootTest
public class FreeMarkerTest {

    @Autowired
    CoursePublishService coursePublishService;

    @Test
    public void testGenerateHtmlByTemplate() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 拿到classpath路径
        String path = this.getClass().getResource("/").getPath();
        // 指定模板目录
        configuration.setDirectoryForTemplateLoading(new File(path + "/templates/"));
        // 指定编码
        configuration.setDefaultEncoding("utf-8");
        // 得到模板
        Template template = configuration.getTemplate("course_template.ftl");
        // 数据信息
        CoursePreviewDto coursePreviewDto = coursePublishService.getCoursePreviewInfo(117L);
        HashMap<String, Object> map = new HashMap<>();
        map.put("model", coursePreviewDto);
        // 模板，数据
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
        // 输入流
        InputStream inputStream = IOUtils.toInputStream(html, "utf-8");
        // 输出流
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\Test\\learning-online\\upload\\117.html"));
        // 使用流将html写入文件
        IOUtils.copy(inputStream, fileOutputStream);
    }
}
