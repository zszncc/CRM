package com.xxxx.crm.service;

import com.github.pagehelper.util.StringUtil;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.model.UserModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserSerivce  extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;
    public UserModel userLogin(String userName, String userPwd){
        AssertUtil.isTrue(StringUtil.isEmpty(userName),"用户名不得为空");
        AssertUtil.isTrue(StringUtil.isEmpty(userPwd),"密码不得为空");
        User user=userMapper.queryUserByName(userName);
        AssertUtil.isTrue(user==null,"用户名不存在");
        userPwd= Md5Util.encode(userPwd);
        AssertUtil.isTrue(!userPwd.equals(user.getUserPwd()),"密码错误");
        return modelUser(user);
    }

    @Transactional(propagation = Propagation.REQUIRED )
    public void updateUserPassword(Integer userId,String oldPassword,String newPassword,String confirmPassword){
        User user= userMapper.selectByPrimaryKey(userId);
        AssertUtil.isTrue(user==null,"待更新记录不存在");
        confirmPassword= Md5Util.encode(confirmPassword);
        oldPassword= Md5Util.encode(oldPassword);
        newPassword= Md5Util.encode(newPassword);
        AssertUtil.isTrue(!oldPassword.equals(user.getUserPwd()),"原密码错误");
        AssertUtil.isTrue(oldPassword.equals(newPassword),"新密码不得与原密码保持一致");
        AssertUtil.isTrue(!newPassword.equals(confirmPassword),"新密码必须与确认密码保持一致!");
        user.setUserPwd(newPassword);
        AssertUtil.isTrue(userMapper.updateByPrimaryKey(user)<1,"用户密码更新失败");
    }
    public UserModel modelUser(User user){
        UserModel userModel=new UserModel();
        userModel.setUserName(user.getUserName());
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }

    public List<Map<String,Object>> queryAllSales(){
        return userMapper.queryAllSales();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        AssertUtil.isTrue(null==user.getUserName(),"姓名不得为空");
        System.out.println(userMapper.queryUserByName(user.getUserName()));
        AssertUtil.isTrue(userMapper.queryUserByName(user.getUserName())!=null,"该用户昵称已存在");
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()),"用户邮箱不得为空");
        AssertUtil.isTrue(StringUtils.isBlank(user.getPhone()),"用户电话不得为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()),"手机号格式不正确");
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        AssertUtil.isTrue(userMapper.insertSelective(user)!=1,"添加失败");
        User temp=userMapper.queryUserByName(user.getUserName());
        relationUserRole(temp.getId(),temp.getRoleIds());
    }

    private void relationUserRole(Integer userId, String roleIds) {
        Integer count=userRoleMapper.countUserRoleByUserId(userId);
        System.out.println(count);
        System.out.println(roleIds);
        if(count>0){
            AssertUtil.isTrue(!userRoleMapper.deleteUserRoleByUserId(userId).equals(count),"用户角色分配失败");
        }
        if(StringUtils.isNotBlank(roleIds)){
            System.out.println(roleIds);
            List<UserRole> userRoleList=new ArrayList<>();
            String[] userRole=roleIds.split(",");
            for (String s : userRole) {
                UserRole userRole1=new UserRole();
                userRole1.setUserId(userId);
                userRole1.setRoleId(Integer.parseInt(s));
                userRole1.setCreateDate(new Date());
                userRole1.setUpdateDate(new Date());
                userRoleList.add(userRole1);
                System.out.println(s);
            }
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList)!=userRoleList.size(),"用户角色关系添加失败");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user) {
        AssertUtil.isTrue(null==user.getUserName(),"姓名不得为空");
        User temp=userMapper.queryUserByName(user.getUserName());
        AssertUtil.isTrue(temp!=null&&!temp.getId().equals(user.getId()),"该用户昵称已存在");
        AssertUtil.isTrue(StringUtils.isBlank(user.getEmail()),"用户邮箱不得为空");
        AssertUtil.isTrue(StringUtils.isBlank(user.getPhone()),"用户电话不得为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(user.getPhone()),"手机号格式不正确");
        user.setUpdateDate(new Date());
        user.setIsValid(1);
        user.setCreateDate(temp.getCreateDate());
        user.setUserPwd(temp.getUserPwd());
        AssertUtil.isTrue(userMapper.updateByPrimaryKey(user)!=1,"用户更新失败");
        relationUserRole(user.getId(),user.getRoleIds());
    }

    @Transactional(propagation = Propagation.REQUIRED )
    public void deleteUser(Integer[] ids) {
            AssertUtil.isTrue(null==ids||ids.length<1,"待删除记录不存在");
            AssertUtil.isTrue(userMapper.deleteBatch(ids)!=ids.length,"删除记录失败");
        for (Integer id : ids) {
            Integer count=userRoleMapper.countUserRoleByUserId(id);
            if(count>0){
                AssertUtil.isTrue(!userRoleMapper.deleteUserRoleByUserId(id).equals(count),"用户角色分配失败");
            }
        }
    }
}
