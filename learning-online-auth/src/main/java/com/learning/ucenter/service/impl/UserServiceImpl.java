package com.learning.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.ucenter.mapper.XcUserMapper;
import com.learning.ucenter.model.dto.AuthParamsDto;
import com.learning.ucenter.model.dto.XcUserExt;
import com.learning.ucenter.model.po.XcUser;
import com.learning.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author HH
 * @version 1.0
 * @description TODO
 * @date 2023/7/10 10:09
 **/
@Slf4j
@Service
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;
    @Autowired
    ApplicationContext applicationContext;

    /**
     * 查询用户信息组成用户身份信息
     *
     * @param s AuthParamsDto类型的json数据
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        // 传入请求认证参数就是AuthParamsDto
        AuthParamsDto authParamsDto = null;
        try {
            //将认证参数转为AuthParamsDto类型
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            log.info("认证请求不符合项目要求:{}", s);
            throw new RuntimeException("认证请求数据格式不对");
        }

        //认证方法
        String authType = authParamsDto.getAuthType();
        AuthService authService = applicationContext.getBean(authType + "_authservice", AuthService.class);
        XcUserExt user = authService.execute(authParamsDto);
        return getUserPrincipal(user);
    }


    /**
     * 查询用户信息
     *
     * @param user 用户id，主键
     * @return 用户信息
     */
    public UserDetails getUserPrincipal(XcUserExt user) {
        //用户权限,如果不加报Cannot pass a null GrantedAuthority collection
        String[] authorities = {"p1"};
        String password = user.getPassword();
        //为了安全在令牌中不放密码
        user.setPassword(null);
        //将user对象转json
        String userString = JSON.toJSONString(user);
        //创建UserDetails对象
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(authorities).build();
        return userDetails;
    }


}
