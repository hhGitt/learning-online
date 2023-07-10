package com.learning.ucenter.service.impl;

import com.learning.ucenter.model.dto.AuthParamsDto;
import com.learning.ucenter.model.dto.XcUserExt;
import com.learning.ucenter.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * @author HH
 * @version 1.0
 * @description 微信认证
 * @date 2023/7/10 10:50
 **/
@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}
