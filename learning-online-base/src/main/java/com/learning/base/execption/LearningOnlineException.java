package com.learning.base.execption;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author HH
 * @version 1.0
 * @description 自定义异常类型
 * @date 2023/7/1 09:22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LearningOnlineException extends RuntimeException {
    private String errMessage;

    public static void cast(String message) {
        throw new LearningOnlineException(message);
    }

    public static void cast(CommonError commonError) {
        throw new LearningOnlineException(commonError.getErrMessage());
    }
}
