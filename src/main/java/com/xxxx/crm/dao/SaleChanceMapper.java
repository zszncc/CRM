package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.vo.SaleChance;

public interface SaleChanceMapper extends BaseMapper<SaleChance,Integer> {

    int insert(SaleChance record);
    int updateByPrimaryKey(SaleChance record);
}