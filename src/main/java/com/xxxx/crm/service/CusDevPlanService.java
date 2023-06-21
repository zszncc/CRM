package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.CusDevPlanMapper;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.query.CusDevPlanQuery;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.CusDevPlan;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {
    @Resource
    private CusDevPlanMapper cusDevPlanMapper;

    @Resource
    private SaleChanceMapper saleChanceMapper;

    public Map<String, Object> queryCusDevPlanByParams(CusDevPlanQuery query){
        Map<String,Object> map=new HashMap<>();
        PageHelper.startPage(query.getPage(),query.getLimit());
        PageInfo<CusDevPlan> pageInfo=new PageInfo<>(cusDevPlanMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void addCusDevPlan(CusDevPlan cusDevPlan) {
        System.out.println(cusDevPlan.getSaleChanceId());
        System.out.println(cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getSaleChanceId()));
        AssertUtil.isTrue(cusDevPlan.getSaleChanceId()==null||saleChanceMapper.selectByPrimaryKey(cusDevPlan.getSaleChanceId())==null,"数据异常");
        AssertUtil.isTrue(cusDevPlan.getPlanItem()==null,"计划内容不能为空");
        AssertUtil.isTrue(cusDevPlan.getPlanDate()==null,"计划时间不能为空");
        cusDevPlan.setIsValid(1);
        cusDevPlan.setPlanDate(new Date());
        cusDevPlan.setUpdateDate(new Date());
        cusDevPlan.setCreateDate(new Date());
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan)<1,"添加失败");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        AssertUtil.isTrue(null==cusDevPlan.getId()||cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId())==null,"数据异常，请重试");
        AssertUtil.isTrue(cusDevPlan.getPlanItem()==null,"计划内容不能为空");
        AssertUtil.isTrue(cusDevPlan.getPlanDate()==null,"计划时间不能为空");
        cusDevPlan.setIsValid(1);
        cusDevPlan.setUpdateDate(new Date());
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"更新失败");
    }

    public void deleteCusDevPlan(Integer id) {
        AssertUtil.isTrue(null==id,"带删除记录为空");
        CusDevPlan cusDevPlan= cusDevPlanMapper.selectByPrimaryKey(id);
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)<1,"删除失败");
    }
}
