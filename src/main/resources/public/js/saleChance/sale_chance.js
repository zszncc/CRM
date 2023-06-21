layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    $(".search_btn").click(function () {
        table.reload('saleChanceTable', {
            where: { //设定异步数据接⼝的额外参数，任意设
                customerName: $("input[name='customerName']").val(), // 客户名
                createMan: $("input[name='createMan']").val(), // 创建⼈
                state: $("#state").val() // 状态
            }
            ,page: {
                curr: 1 // 重新从第 1 ⻚开始
            }
        }); // 只重载数据
    });
    /**
     * 营销机会列表展示
     */
    var tableIns = table.render({
        id:'saleChanceTable',
        elem: '#saleChanceList', // 表格绑定的ID
        url : ctx + '/sale_chance/list', // 访问数据的地址
        cellMinWidth : 95,
        page : true, // 开启分⻚
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'chanceSource', title: '机会来源',align:"center"},
            {field: 'customerName', title: '客户名称', align:'center'},
            {field: 'cgjl', title: '成功⼏率', align:'center'},
            {field: 'overview', title: '概要', align:'center'},
            {field: 'linkMan', title: '联系⼈', align:'center'},
            {field: 'linkPhone', title: '联系电话', align:'center'},
            {field: 'description', title: '描述', align:'center'},
            {field: 'createMan', title: '创建⼈', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center'},
            {field: 'uname', title: '指派⼈', align:'center'},
            {field: 'assignTime', title: '分配时间', align:'center'},
            {field: 'updateDate', title: '更新时间', align:'center'},
            {field: 'state', title: '分配状态', align:'center',templet:function(d){
                    return formatterState(d.state);
                }},
            {field: 'devResult', title: '开发状态', align:'center',templet:function (d) {
                    return formatterDevResult(d.devResult);
                }},
            {title: '操作', templet:'#saleChanceListBar',fixed:"right",align:"center",
                minWidth:150}
        ]]
    });

    /**
     * 头部⼯具栏 监听事件
     */
    table.on('toolbar(saleChances)', function(obj){
            switch(obj.event){
        case 'add':
            // 点击添加按钮，打开添加营销机会的对话框
            openAddOrUpdateSaleChanceDialog();
            break;
        case 'del':
            // 点击删除按钮
            delSaleChance(obj);
            break;
        };
    });

    function delSaleChance(data){
        var checkStatus=table.checkStatus("saleChanceTable");
        console.log(checkStatus);
        var saleChanceData=checkStatus.data;
        if(saleChanceData.length<1){
            layer.msg("请选择需要删除记录",{icon:5});
            return;
        }
        layer.confirm('确认要删除该记录吗?',{icon:3,title:'营销机会管理'},function (index){
            layer.close(index);
            var ids="";
            for (let i = 0; i < saleChanceData.length; i++) {
                if(i<saleChanceData.length-1) {
                    ids = ids + "ids=" + saleChanceData[i].id + "&";
                }
                ids = ids + "ids=" + saleChanceData[i].id;
            }
            $.ajax({
                type:'post',
                url:ctx+'/sale_chance/delete',
                data:ids,
                success: function (result){
                    if(result.code==200){
                        layer.msg("删除成功",{icon:6});
                        tableIns.reload();
                    }else{
                        layer.msg("删除失败",{icon:5});
                    }
                }
            })
        })
    }
    /**
     * 表格⾏ 监听事件
     * saleChances为table标签的lay-filter 属性值
     */
    table.on('tool(saleChances)', function(obj) {
        var data = obj.data; // 获得当前⾏数据
        var layEvent = obj.event; // 获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
        // 判断事件类型
        if (layEvent === 'edit') { // 编辑操作
            // 获取当前要修改的⾏的id
            var saleChanceId = data.id;
            // 点击表格⾏的编辑按钮，打开更新营销机会的对话框
            openAddOrUpdateSaleChanceDialog(saleChanceId);
        }else if(layEvent==='del'){
            layer.confirm('确认要删除该记录吗?',{icon:3,title:'营销机会管理'},function (index){
                layer.close(index);
                $.ajax({
                    type:'post',
                    url:ctx+'/sale_chance/delete',
                    data:{
                        ids:data.id
                    },
                    success: function (result){
                        if(result.code==200){
                            layer.msg("删除成功",{icon:6});
                            tableIns.reload();
                        }else{
                            layer.msg("删除失败",{icon:5});
                        }
                    }
                })
            })
        }
    });
    /**
     * 打开添加营销机会的对话框
     */
    /**
     * 打开添加营销机会的对话框
     */
    function openAddOrUpdateSaleChanceDialog(saleChanceId) {
            var title = "<h2>营销机会管理 - 机会添加</h2>";
            var url = ctx + "/sale_chance/addOrUpdateSaleChancePage";
            // 通过id判断是添加操作还是修改操作
            if (saleChanceId!=null&&saleChanceId!="") {
                // 如果id不为空，则为修改操作
            title = "<h2>营销机会管理 - 机会更新</h2>";
            url = url + "?id=" + saleChanceId;
        }
        layui.layer.open({
            title:title,
            type:2,
            content: url,
            area:["500px","620px"],
            maxmin:true
        });
    }
    /**
     * 格式化分配状态
     * 0 - 未分配
     * 1 - 已分配
     * 其他 - 未知
     * @param state
     * @returns {string}
     */
    function formatterState(state){
        if(state==0) {
            return "<div style='color: yellow'>未分配</div>";
        } else if(state==1) {
            return "<div style='color: green'>已分配</div>";
        } else {
            return "<div style='color: red'>未知</div>";
        }
    }
    /**
     * 格式化开发状态
     * 0 - 未开发
     * 1 - 开发中
     * 2 - 开发成功
     * 3 - 开发失败
     * @param value
     * @returns {string}
     */
    /**
     * 绑定搜索按钮的点击事件
     */


    function formatterDevResult(value){
        if(value == 0) {
            return "<div style='color: yellow'>未开发</div>";
        } else if(value==1) {
            return "<div style='color: #00FF00;'>开发中</div>";
        } else if(value==2) {
            return "<div style='color: #00B83F'>开发成功</div>";
        } else if(value==3) {
            return "<div style='color: red'>开发失败</div>";
        } else {
            return "<div style='color: #af0000'>未知</div>"
        }
    }
});
