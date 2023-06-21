var zTreeObj;
$(function () {
    loadModuleInfo();
});
function loadModuleInfo() {
    // zTree 的参数配置，深⼊使⽤请参考 API ⽂档（setting 配置详解）
    var setting = {
        data: {
            simpleData: {
                enable: true
            }
        },
        view:{
            showLine: false
            // showIcon: false
        },
        check: {
            enable: true,
            chkboxType: { "Y": "ps", "N": "ps" }
        },
        callback: {
            onCheck: zTreeOnCheck
        }
    };
    $.ajax({
        type:"get",
        url:ctx+"/module/queryAllModules?roleId="+$("#roleId").val(),
        dataType:"json",
        success:function (data) {
            zTreeObj=$.fn.zTree.init($("#test1"), setting, data);
        }
    })
}
function zTreeOnCheck(event, treeId, treeNode) {
    var nodes= zTreeObj.getCheckedNodes(true);
    var roleId=$("#roleId").val();
    var mids="mids=";
    if(nodes.length>0) {
        for (var i = 0; i < nodes.length; i++) {
            if (i < nodes.length - 1) {
                mids = mids + nodes[i].id + "&mids=";
            } else {
                mids = mids + nodes[i].id;
            }
        }
    }
    $.ajax({
        type:"post",
        url:ctx+"/role/addGrant",
        data:mids+"&roleId="+roleId,
        dataType:"json",
        success:function (data) {
            console.log(data);
        }
    })
}