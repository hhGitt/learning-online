package com.learning.content.feginclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/8 15:05
 **/
@Slf4j
@Component
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {
    // 拿到熔断异常信息
    @Override
    public MediaServiceClient create(Throwable throwable) {
        return new MediaServiceClient() {
            // 发生熔断上传服务调用此方法执行降级逻辑
            @Override
            public String uploadFile(MultipartFile upload, String objectName) {
                log.debug("远程调用上传文件的接口发生熔断:{}", throwable.toString(), throwable);
                return null;
            }
        };
    }
}
