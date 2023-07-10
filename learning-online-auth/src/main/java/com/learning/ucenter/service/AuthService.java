package com.learning.ucenter.service;

import com.learning.ucenter.model.dto.AuthParamsDto;
import com.learning.ucenter.model.dto.XcUserExt;

/**
 * @author HH
 * @version 1.0
 * @description 统一认证接口
 * @date 2023/7/10 10:46
 **/
public interface AuthService {
    /**
     * 认证方法
     *
     * @param authParamsDto 认证参数
     * @return XcUser 用户信息
     */
    XcUserExt execute(AuthParamsDto authParamsDto);

}
