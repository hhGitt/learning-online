package com.learning.content;

import com.learning.content.config.MultipartSupportConfig;
import com.learning.content.feginclient.MediaServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author HH
 * @version 1.0
 * @description 远程调用测试
 * @date 2023/7/8 14:40
 **/
@SpringBootTest
public class FeignUploadTest {
    @Autowired
    MediaServiceClient mediaServiceClient;

    @Test
    public void test() throws Exception {
        // 将file转为MultipartFile
        File file = new File("D:\\Test\\learning-online\\upload\\117.html");
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String s = mediaServiceClient.uploadFile(multipartFile, "course/117.html");
        if (s == null){
            System.out.println("降级了");
        }
    }
}
