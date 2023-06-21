package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {

    int insert(User record);
    int updateByPrimaryKey(User record);

    User queryUserByName(String name);
    public List<Map<String, Object>> queryAllSales();
}