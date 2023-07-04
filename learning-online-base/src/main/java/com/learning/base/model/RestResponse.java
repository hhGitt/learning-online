package com.learning.base.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/4 16:56
 **/
@Data
@ToString
public class RestResponse<T> {

    @ApiModelProperty("响应编码，0正常，-1错误")
    private int code;

    @ApiModelProperty("响应提示信息")
    private String msg;

    @ApiModelProperty("响应内容")
    private T result;

    public RestResponse() {
        this(0, "success");
    }

    public RestResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 正常响应数据
     *
     * @param result 数据
     * @param <T>    泛型
     * @return 封装好的数据
     */
    public static <T> RestResponse<T> success(T result) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        return response;
    }

    /**
     * 正常响应数据
     *
     * @param result 数据
     * @param msg    响应信息
     * @param <T>    泛型
     * @return 封装好的数据
     */
    public static <T> RestResponse<T> success(T result, String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }

    /**
     * 错误响应数据
     *
     * @param result 数据
     * @param msg    响应信息
     * @param <T>    泛型
     * @return 封装好的数据
     */
    public static <T> RestResponse<T> fail(T result, String msg) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(-1);
        response.setResult(result);
        response.setMsg(msg);
        return response;
    }
}
