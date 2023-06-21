package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper extends BaseMapper<Role,Integer> {
    List<Map<String, Object>> queryAllRoles(Integer userId);

    Role selectByRoleName(String roleName);

    int updateByPrimaryKey(Role role);
}