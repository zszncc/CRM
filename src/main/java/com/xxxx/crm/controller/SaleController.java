package com.xxxx.crm.controller;

import com.xxxx.crm.annoation.RequirePermission;
import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
@RequestMapping("sale_chance")
@Controller
public class SaleController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;

    @ResponseBody
    @RequestMapping("list")
    @RequirePermission(code = "101001")
    public Map<String,Object> selectByParams(SaleChanceQuery query,Integer flag,HttpServletRequest request){
        if(flag!=null&&flag==1){
            query.setState(StateStatus.STATED.getType());
            Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
            query.setAssignMan(userId);
        }
        return saleChanceService.selectByParams(query);
    }
    @RequirePermission(code = "1010")
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    @RequestMapping("addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id, Model model){
        if(null!=id){
            SaleChance saleChance=saleChanceService.selectByPrimaryKey(id);
            model.addAttribute("saleChance",saleChance);
        }
        return "salechance/add_update";
    }
    @ResponseBody
    @RequestMapping("save")
    @RequirePermission(code = "101002")
    public ResultInfo saveSaleChance(HttpServletRequest request, SaleChance saleChance){
        String createName= CookieUtil.getCookieValue(request,"userName");
        saleChance.setCreateMan(createName);
        saleChanceService.saveSaleChance(saleChance);
        return success("添加成功");
    }

    @ResponseBody
    @RequestMapping("update")
    @RequirePermission(code = "101004")
    public ResultInfo updateSaleChance(HttpServletRequest request, SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success("更新成功");
    }

    @RequestMapping("delete")
    @ResponseBody
    @RequirePermission(code = "101003")
    public ResultInfo queryAllSales(Integer []ids){
        saleChanceService.deleteSaleChance(ids);
        return success("删除成功");
    }

    @RequestMapping("updateSaleChanceDevResult")
    @ResponseBody
        public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("更新成功");
    }
}
