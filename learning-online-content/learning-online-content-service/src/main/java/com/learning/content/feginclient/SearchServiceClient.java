package com.learning.content.feginclient;

import com.learning.content.model.dto.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author HH
 * @version 1.0
 * @description 远程调用el搜索服务
 * @date 2023/7/9 10:34
 **/
@FeignClient(value = "search",fallbackFactory = SearchServiceClientFallbackFactory.class)

public interface SearchServiceClient {

        @PostMapping("/search/index/course")
        public Boolean add(@RequestBody CourseIndex courseIndex);

}
