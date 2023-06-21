package com.xxxx.crm;

import com.alibaba.fastjson.JSON;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.AuthException;
import com.xxxx.crm.exceptions.NoLoginException;
import com.xxxx.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) {
        ModelAndView mv=new ModelAndView();
        if(e instanceof NoLoginException){
            mv.setViewName("redirect:/index");
            return mv;
        }
        mv.addObject("code",500);
        mv.addObject("msg","操作失败");

        if(o instanceof HandlerMethod){
            HandlerMethod handlerMethod= (HandlerMethod) o;
            ResponseBody responseBody=handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            if(null==responseBody){
                //返回视图
                if(e instanceof ParamsException){
                    ParamsException pe= (ParamsException) e;
                    mv.addObject("code",pe.getCode());
                    mv.addObject("msg",pe.getMsg());
                }else if(e instanceof AuthException){
                    AuthException a= (AuthException) e;
                    mv.addObject("code",a.getCode());
                    mv.addObject("msg",a.getMsg());
                }
                return mv;
            }else{
                //返回JSON
                ResultInfo resultInfo=new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统异常，请稍后重试。。。。");
                if(e instanceof ParamsException){
                    ParamsException pe= (ParamsException) e;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }else if(e instanceof AuthException){
                    AuthException a= (AuthException) e;
                    resultInfo.setCode(a.getCode());
                    resultInfo.setMsg(a.getMsg());
                }
                // 设置响应类型和编码格式 （响应JSON格式）
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out=null;
                try{
                    out=response.getWriter();
                    out.write(JSON.toJSONString(resultInfo));
                    out.flush();

                }catch (Exception e1){
                    e1.printStackTrace();
                }finally {
                    if(out!=null){
                        out.close();
                    }
                }
                return null;
            }
        }




        return mv;
    }
}
