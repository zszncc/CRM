layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;
    var tableIns = table.render({
        id:'userTable',
        elem: '#userList', // 表格绑定的ID
        url : ctx + '/user/list', // 访问数据的地址
        cellMinWidth : 95,
        page : true, // 开启分⻚
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '⽤户名', minWidth:50, align:"center"},
            {field: 'email', title: '⽤户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '⽤户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });

    $(".search_btn").click(function () {
        table.reload('userTable', {
            where: { //设定异步数据接⼝的额外参数，任意设
                userName: $("input[name='userName']").val(), //⽤户名
                email: $("input[name='email']").val(), //邮箱
                phone: $("input[name='phone']").val() //⼿机号
            }
            ,page: {
                curr: 1 // 重新从第 1 ⻚开始
            }
        }); // 只重载数据
    });

    /**
     * 头部⼯具栏事件
     */
    table.on("toolbar(users)", function (obj) {
        var checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case "add":
                openAddOrUpdateUserDialog();
                break;
            case "del":
                // 点击删除按钮
                delUser(obj);
                break;
        }
    });

    function delUser(data){
        var checkStatus=table.checkStatus("userTable");
        var userData=checkStatus.data;
        if(userData.length<1){
            layer.msg("请选择需要删除记录",{icon:5});
            return;
        }
        layer.confirm('确认要删除该记录吗?',{icon:3,title:'用户管理'},function (index){
            layer.close(index);
            var ids="";
            for (let i = 0; i < userData.length; i++) {
                if(i<userData.length-1) {
                    ids = ids + "ids=" + userData[i].id + "&";
                }
                ids = ids + "ids=" + userData[i].id;
            }
            $.ajax({
                type:'post',
                url:ctx+'/user/delete',
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
     * ⾏监听事件
     */
    table.on("tool(users)", function(obj){
        var layEvent = obj.event;
        // 监听编辑事件
        if(layEvent === "edit") {
            openAddOrUpdateUserDialog(obj.data.id);
        }
        if(layEvent === "del") {
            layer.confirm('确认要删除该记录吗?',{icon:3,title:'用户管理'},function (index){
                layer.close(index);
                $.ajax({
                    type:'post',
                    url:ctx+'/user/delete',
                    data:{
                        ids:obj.data.id
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
     * 打开⽤户添加或更新对话框
     */
    function openAddOrUpdateUserDialog(userId) {
        var url = ctx + "/user/addOrUpdateUserPage";
        var title = "⽤户管理-⽤户添加";
        if(userId){
            url = url + "?id="+userId;
            title = "⽤户管理-⽤户更新";
        }
        layui.layer.open({
            title : title,
            type : 2,
            area:["650px","400px"],
            maxmin:true,
            content : url
        });
    }
});