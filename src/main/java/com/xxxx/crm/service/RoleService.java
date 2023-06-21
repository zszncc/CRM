package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.dao.RoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Permission;
import com.xxxx.crm.vo.Role;
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
public class RoleService extends BaseService<Role,Integer> {
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void roleAdd(Role role){
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名称不得为空");
        Role temp=roleMapper.selectByRoleName(role.getRoleName());
        AssertUtil.isTrue(null!=temp,"角色名称已存在");
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        role.setIsValid(1);
        AssertUtil.isTrue(roleMapper.insertSelective(role)!=1,"角色添加失败");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer id) {
        AssertUtil.isTrue(null==id,"待删除记录为空");
        AssertUtil.isTrue(null==roleMapper.selectByPrimaryKey(id),"待删除记录为空");
        Role role=roleMapper.selectByPrimaryKey(id);
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        AssertUtil.isTrue(roleMapper.updateByPrimaryKey(role)!=1,"删除失败");
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role) {
        AssertUtil.isTrue(null==role.getRoleName(),"角色名称不得为空");
        Role temp=roleMapper.selectByRoleName(role.getRoleName());
        AssertUtil.isTrue(null!=temp&& !temp.getId().equals(role.getId()),"角色昵称已存在");
        AssertUtil.isTrue(null==role.getRoleRemark(),"角色备注不得为空");
        role.setIsValid(1);
        role.setUpdateDate(new Date());
        role.setCreateDate(roleMapper.selectByPrimaryKey(role.getId()).getCreateDate());
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)!=1,"更新失败");
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId, Integer[] mids) {
        Integer count=permissionMapper.countPermissionByRoleId(roleId);
        if(count>0){
            permissionMapper.deletePermissonByRoleId(roleId);
        }
        List<Permission> permissionList=new ArrayList<>();
        if(null!=mids&&mids.length>0){
            for (Integer mid : mids) {
                Permission permission=new Permission();
                permission.setRoleId(roleId);
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                permission.setModuleId(mid);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permissionList.add(permission);
            }
        }
        AssertUtil.isTrue(permissionMapper.insertBatch(permissionList)!=permissionList.size(),"角色授权失败");
    }
}
