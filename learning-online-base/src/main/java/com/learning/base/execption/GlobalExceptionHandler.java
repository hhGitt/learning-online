package com.learning.base.execption;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/1 09:28
 **/
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 对项目自定义异常进行处理
     * @param e 异常
     * @return 返回异常信息
     */
    @ExceptionHandler(LearningOnlineException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse customException(LearningOnlineException e) {
        String errMessage = e.getErrMessage();
        // 记录系统异常
        log.error("系统异常{}", errMessage, e);
        // 解析异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }

    /**
     * 系统异常处理
     * @param e 异常
     * @return 异常信息
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse exception(Exception e) {
        // 记录系统异常
        log.error("系统异常{}", e.getMessage(), e);
        // 解析异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(CommonError.UNKOWN_ERROR.getErrMessage());
        return restErrorResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<Object> errors = new ArrayList<>();
        bindingResult.getFieldErrors().forEach(item->errors.add(item.getDefaultMessage()));
        // 将errors错误拼接起来
        String errMessage = StringUtils.join(errors, ",");
        // 记录系统异常
        log.error("系统异常{}", errMessage, e);
        // 解析异常信息
        RestErrorResponse restErrorResponse = new RestErrorResponse(errMessage);
        return restErrorResponse;
    }
}
