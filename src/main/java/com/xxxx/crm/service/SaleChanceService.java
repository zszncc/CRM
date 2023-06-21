package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;

    public Map<String, Object> selectByParams(SaleChanceQuery query){
        Map<String,Object> map=new HashMap<>();
        PageHelper.startPage(query.getPage(),query.getLimit());
        PageInfo<SaleChance> pageInfo=new PageInfo<>(saleChanceMapper.selectByParams(query));
        map.put("code",0);
        map.put("msg","success");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRED )
    public void saveSaleChance(SaleChance saleChance){
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getCustomerName()),"请输入客户名");
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getLinkMan()),"请输入联系人");
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getLinkPhone()),"请输入联系人手机号");
        AssertUtil.isTrue(!PhoneUtil.isMobile(saleChance.getLinkPhone()),"手机号格式不正确");
        saleChance.setState(StateStatus.UNSTATE.getType());
        saleChance.setDevResult(DevResult.UNDEV.getStatus());
        if(StringUtils.isNotBlank(saleChance.getAssignMan())){
            System.out.println(saleChance.getAssignMan());
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
            saleChance.setAssignTime(new Date());
        }
        saleChance.setIsValid(1);
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        AssertUtil.isTrue(insertSelective(saleChance)<1,"添加失败");
    }

    @Transactional(propagation = Propagation.REQUIRED )
    public void updateSaleChance(SaleChance saleChance){
        SaleChance temp=saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(null==temp,"待更新记录不存在");
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getCustomerName()),"请输入客户名");
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getLinkMan()),"请输入联系人");
        AssertUtil.isTrue(StringUtils.isBlank(saleChance.getLinkPhone()),"请输入联系人手机号");
        AssertUtil.isTrue(!PhoneUtil.isMobile(saleChance.getLinkPhone()),"手机号格式不正确");
        saleChance.setUpdateDate(new Date());
        if(StringUtils.isBlank(temp.getAssignMan())&&StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
            saleChance.setState(StateStatus.STATED.getType());
        }else if(StringUtils.isBlank(saleChance.getAssignMan())&&StringUtils.isNotBlank(temp.getAssignMan())){
            saleChance.setAssignTime(null);
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setCreateMan("");
        }
        if(saleChance.getAssignMan().equals(temp.getAssignMan())){
            saleChance.setAssignTime(temp.getAssignTime());
        }else{
            saleChance.setAssignTime(new Date());
        }
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"更新失败");

    }

    @Transactional(propagation = Propagation.REQUIRED )
    public void deleteSaleChance(Integer []ids){
        AssertUtil.isTrue(null==ids||ids.length<1,"待删除记录不存在");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)!=ids.length,"删除记录失败");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        AssertUtil.isTrue(null==id,"待更新记录不存在");
        SaleChance saleChance=saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null==saleChance,"待更新记录不存在");
        saleChance.setDevResult(devResult);
        saleChance.setUpdateDate(new Date());
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"更新失败");
    }
}
