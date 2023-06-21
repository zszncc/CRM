layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    /**
     * 计划项数据展示
     */

    var tableIns = table.render({
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?saleChanceId='+$("input[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "cusDevPlanListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执⾏效果',align:"center"},
            {field: 'planDate', title: '执⾏时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,
                templet:"#cusDevPlanListBar"}
        ]]
    });

    /**
     * 头部⼯具栏事件
     */
    table.on('toolbar(cusDevPlans)', function(data) {
        function updateSaleChanceDevResult(devResult) {
            layer.confirm("您确认执行开发成功操作吗？", {icon:3, title:"营销机会管理"}, function (index) {
                var sId=$("[name='id']").val();
                // 发送ajax请求
                $.post(ctx + "/sale_chance/updateSaleChanceDevResult", {id:sId,devResult:devResult}, function (data) {
                    if (data.code == 200) {
                        layer.msg("操作成功！",{icon:6});
                        layer.closeAll("iframe");
                        parent.location.reload();
                    } else {
                        layer.msg(data.msg, {icon:5});
                    }
                });
            });
        }

        if (data.event == "add") {
                openAddOrUpdateCusDevPlanDialog();
            } else if (data.event == "success") {
                updateSaleChanceDevResult(2);
            }else if (data.event == "failed") {
                updateSaleChanceDevResult(3);
            }

    });

    /**
     * ⾏监听事件
     */
    table.on("tool(cusDevPlans)", function(obj){
        var layEvent = obj.event;
        // 监听编辑事件
        if(layEvent === "edit") {
            openAddOrUpdateCusDevPlanDialog(obj.data.id);
        } else if (layEvent == "del") {
            // 询问⽤户是否确认删除
            layer.confirm("确定删除当前数据？", {icon:3, title:"开发计划管理"}, function (index) {
                // 发送ajax请求
                $.post(ctx + "/cus_dev_plan/delete", {id:obj.data.id}, function (data) {
                    if (data.code == 200) {
                        layer.msg("操作成功！");
                        // 重新加载表格
                        tableIns.reload();
                    } else {
                        layer.msg(data.msg, {icon:5});
                    }
                });
            });
        }
    });
    function  openAddOrUpdateCusDevPlanDialog(id){
        var url = ctx+"/cus_dev_plan/toAddOrUpdateCusDevPlanPage?sId="+$("[name='id']").val();
        var title = "计划项管理-添加计划项";
        if(id){
            url = url + "&id=" + id;
            title = "计划项管理-更新计划项";
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["500px","300px"],
            maxmin:true,
            content : url
        });
    }
});