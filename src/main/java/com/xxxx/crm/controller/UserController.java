package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserSerivce;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController  extends BaseController {
    @Resource
    private UserSerivce userSerivce;

    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd){
        ResultInfo resultInfo=new ResultInfo();
        UserModel userModel= userSerivce.userLogin(userName,userPwd);
        resultInfo.setResult(userModel);
        return resultInfo;
    }

    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPassword, String newPassword, String confirmPassword){
        ResultInfo resultInfo=new ResultInfo();
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
        userSerivce.updateUserPassword(userId,oldPassword,newPassword,confirmPassword);
        return resultInfo;
    }

    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }

    @RequestMapping("index")
    public String index(){
        return "user/user";
    }

    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String ,Object>> queryAllSales(){
        return userSerivce.queryAllSales();
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String ,Object> queryByParams(UserQuery userQuery){
        return userSerivce.queryByParamsForTable(userQuery);
    }

    @PostMapping("add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userSerivce.addUser(user);
        return success("添加成功");
    }

    @PostMapping("updateUser")
    @ResponseBody
    public ResultInfo updateUser(User user,HttpServletRequest request){
        userSerivce.updateUser(user);
        return success("更新成功");
    }

    @RequestMapping("addOrUpdateUserPage")
    public String toAddOrUpdateUserPage(Integer id,HttpServletRequest request){
        User user=userSerivce.selectByPrimaryKey(id);
        request.setAttribute("user1",user);
        return "user/add_update";
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo queryAllUser(Integer []ids){
       userSerivce.deleteUser(ids);
        return success("删除成功");
    }


}
