package com.learning.content.feginclient;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/8 15:04
 **/
public class MediaServiceClientFallback implements MediaServiceClient{
    @Override
    public String uploadFile(MultipartFile upload, String objectName) {
        return null;
    }
}
