package com.learning.base.execption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author HH
 * @version 1.0
 * @description 错误响应参数包装
 * @date 2023/7/1 09:20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RestErrorResponse implements Serializable {
    // 异常信息
    private String errMessage;
}
