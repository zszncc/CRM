package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.RoleQuery;
import com.xxxx.crm.service.RoleService;
import com.xxxx.crm.vo.Role;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequestMapping("role")
@Controller
public class RoleController extends BaseController {
    @Resource
    private RoleService roleService;

    @RequestMapping("list")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    @GetMapping("roleList")
    @ResponseBody
    public Map<String,Object> selectByRoleName(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }

    @RequestMapping("index")
    public String index(){
        return "role/role";
    }

    @PostMapping("add")
    @ResponseBody
    public ResultInfo addRole(Role role){
        System.out.println("正在添加中");
        roleService.roleAdd(role);
        return success("添加成功");
    }

    @RequestMapping("addOrUpdateRolePage")
    public String toAddOrUpdateRolePage(HttpServletRequest request,Integer id){
        Role role=roleService.selectByPrimaryKey(id);
        request.setAttribute("role",role);
       return "role/add_update";
    }

    @PostMapping("delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer id){
        roleService.deleteRole(id);
        return success("删除成功");
    }

    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        System.out.println("正在更新中");
        roleService.updateRole(role);
        return success("更新成功");
    }

    @ResponseBody
    @RequestMapping("addGrant")
    public ResultInfo addGrant(Integer roleId,Integer  []mids){
        roleService.addGrant(roleId,mids);
        return success("角色授权成功");
    }
}
