package com.xxxx.crm.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    public List<TreeModel> queryAllModules(Integer roleId){
        List<TreeModel> treeModelList=moduleMapper.queryAllModules();
        List<Integer>  permissionIds=permissionMapper.queryRoleHasModuleIdByRoleId(roleId);
        if(null!=permissionIds&&permissionIds.size()>0){
         treeModelList.forEach(treeModel -> {
             if(permissionIds.contains(treeModel.getId())){
                 treeModel.setChecked(true);
             }
                 }
                 );
        }
        return treeModelList;
    }

    public Map<String,Object> queryModuleList(){
        Map<String,Object> map=new HashMap<>();
        List<Module> list=moduleMapper.queryModuleList();
        map.put("code",200);
        map.put("msg","");
        map.put("count",list.size());
        map.put("data",list);
        return map;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module) {
        Integer grade=module.getGrade();
        AssertUtil.isTrue(null==grade||!(grade==0||grade==1||grade==2),"菜单层级不合法");
        AssertUtil.isTrue(module.getModuleName()==null,"模块名称不得为空");
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName()),"模块名称已存在");
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"URL不得为空");
            AssertUtil.isTrue(null!=moduleMapper.queryModuleByGradeAndUrl(grade,module.getUrl()),"菜单名称不可重复");
        }
        if(grade==0){
            module.setParentId(-1);
        }
        if(grade!=0){
            AssertUtil.isTrue(null==module.getParentId(),"父级菜单不得为空");
            AssertUtil.isTrue(null==moduleMapper.selectByPrimaryKey(module.getParentId()),"父级菜单不存在");
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不得为空");
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码已存在");
        module.setIsValid((byte)1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.insertSelective(module)<1,"添加失败");
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module) {
        AssertUtil.isTrue(null==module.getId(),"待更新记录不存在");
        Module temp=moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(null==temp,"待更新记录不存在");
        Integer grade=module.getGrade();
        AssertUtil.isTrue(null==grade||!(grade==0||grade==1||grade==2),"菜单层级不合法");
        AssertUtil.isTrue(module.getModuleName()==null,"模块名称不得为空");
        temp=moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName());
        if(temp!=null){
            AssertUtil.isTrue(!(temp.getId().equals(module.getId())),"该层级下菜单名已存在");
        }
        if(grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"URL不得为空");
            temp=moduleMapper.queryModuleByGradeAndUrl(grade,module.getUrl());
            if(temp!=null){
                AssertUtil.isTrue(!(temp.getId()).equals(module.getId()),"该层级下菜单url已存在");
            }
        }
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不得为空");
        temp=moduleMapper.queryModuleByOptValue(module.getOptValue());
        if(temp!=null){
            AssertUtil.isTrue(!temp.getId().equals(module.getId()),"权限码已存在");
        }
        module.setUpdateDate(new Date());
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)<1,"修改失败");

    }

    public void deleteModule(Integer id) {
        //判断id是否为空
        AssertUtil.isTrue(null==id,"待删除记录不存在");
        Module temp=moduleMapper.selectByPrimaryKey(id);
        //判断记录是否存在
        AssertUtil.isTrue(temp==null,"待删除记录不存在");
        //判断是否存在该菜单下的子菜单
        Integer count=moduleMapper.queryModuleByParentId(id);
        AssertUtil.isTrue(count!=0,"该菜单下存在子菜单，不能删除");
        count=permissionMapper.countPermissionByModuleId(id);
        if(count>0){
            permissionMapper.deletePermissionByModuleId(id);
        }
        temp.setUpdateDate(new Date());
        temp.setIsValid((byte) 0);
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp)<1,"删除失败");
    }
}
