package com.learning.content.feginclient;

import com.learning.content.model.dto.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/9 10:35
 **/
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                throwable.printStackTrace();
                log.debug("调用搜索发生熔断走降级方法,索引信息:{},熔断异常:{}",courseIndex, throwable.getMessage());
                return false;
            }
        };

    }
}
