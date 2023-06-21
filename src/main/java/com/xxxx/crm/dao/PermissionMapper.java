package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    Integer countPermissionByRoleId(Integer roleId);

    void deletePermissonByRoleId(Integer roleId);

    List<Integer> queryRoleHasModuleIdByRoleId(Integer roleId);

    List<String> queryUserHasRoleHasPermissionByUserId(Integer userId);

    Integer countPermissionByModuleId(Integer id);

    void deletePermissionByModuleId(Integer id);
}