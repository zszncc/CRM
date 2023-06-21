package com.xxxx.crm.interceptors;

import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.service.UserSerivce;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.utils.UserIDBase64;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private UserSerivce userSerivce;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
        if(null==userId||null==userSerivce.selectByPrimaryKey(userId)){
            throw new NoLoginException();
        }
      return true;
    }
}
