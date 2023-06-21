layui.use(['table','layer'],function(){
       var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

       //⻆⾊列表展示
       var tableIns = table.render({
              elem: '#roleList',
              url : ctx+'/role/roleList',
              cellMinWidth : 95,
              page : true,
              height : "full-125",
              limits : [10,15,20,25],
              limit : 10,
              toolbar: "#toolbarDemo",
              id : "roleListTable",
              cols : [[
                     {type: "checkbox", fixed:"left", width:50},
                     {field: "id", title:'编号',fixed:"true", width:80},
                     {field: 'roleName', title: '⻆⾊名', minWidth:50, align:"center"},
                     {field: 'roleRemark', title: '⻆⾊备注', minWidth:100, align:'center'},
                     {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
                     {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
                     {title: '操作', minWidth:150, templet:'#roleListBar',fixed:"right",align:"center"}
              ]]
       });

       $(".search_btn").click(function () {
              table.reload('roleListTable', {
                     where: { //设定异步数据接⼝的额外参数，任意设
                            roleName: $("input[name='roleName']").val(), // 角色名称
                     }
                     ,page: {
                            curr: 1 // 重新从第 1 ⻚开始
                     }
              }); // 只重载数据
       });

       table.on('toolbar(roles)', function(obj){
              var checkStatus = table.checkStatus(obj.config.id);
              switch(obj.event){
                     case 'add':
                            // 点击添加按钮，打开添加营销机会的对话框
                            openAddOrUpdateSaleChanceDialog();
                            break;
                     case 'grant':
                            // 点击授权按钮
                            openAddGrantDailog(checkStatus.data);
                            break;
              };
       });

       function openAddGrantDailog(datas){
              if(datas.length==0){
                     layer.msg("请选择待授权⻆⾊记录!", {icon: 5});
                     return;
              }
              if(datas.length>1){
                     layer.msg("暂不⽀持批量⻆⾊授权!", {icon: 5});
                     return;
              }
              var url = ctx+"/module/toAddGrantPage?roleId="+datas[0].id;
              var title="⻆⾊管理-⻆⾊授权";
              layui.layer.open({
                     title : title,
                     type : 2,
                     area:["600px","380px"],
                     maxmin:true,
                     content : url
              });
       }

       table.on('tool(roles)', function(obj) {
              var data = obj.data; // 获得当前⾏数据
              console.log(data);
              var layEvent = obj.event; // 获得 lay-event 对应的值（也可以是表头的 event 参数对应的值）
              // 判断事件类型
              if (layEvent === 'edit') { // 编辑操作
                     // 获取当前要修改的⾏的id
                     var roleId = data.id;
                     // 点击表格⾏的编辑按钮，打开更新营销机会的对话框
                     openAddOrUpdateSaleChanceDialog(roleId);
              }else if(layEvent==='del'){
                     layer.confirm('确认要删除该记录吗?',{icon:3,title:'营销机会管理'},function (index){
                            layer.close(index);
                            $.ajax({
                                   type:'post',
                                   url:ctx+'/role/delete',
                                   data:{
                                          id:data.id
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
       function openAddOrUpdateSaleChanceDialog(id) {
              var title = "<h2>角色管理 - 角色添加</h2>";
              var url = ctx + "/role/addOrUpdateRolePage";
              // 通过id判断是添加操作还是修改操作
              if (id!=null&&id!=="") {
                     // 如果id不为空，则为修改操作
                     title = "<h2>角色管理 - 角色更新</h2>";
                     url = url + "?id=" + id;
              }
              layui.layer.open({
                     title:title,
                     type:2,
                     content: url,
                     area:["500px","620px"],
                     maxmin:true
              });
       }
        


});
