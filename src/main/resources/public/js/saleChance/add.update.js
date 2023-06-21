layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;

    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function () {
        // 先得到当前iframe层的索引
        var index = parent.layer.getFrameIndex(window.name);
        // 再执⾏关闭
        parent.layer.close(index);
    });

    $.ajax({
        type:'get',
        url:ctx+'/user/queryAllSales',
        data:{},
        success:function (data){
            if(data!=null){
                var assignManId=$("#assignManId").val();
                console.log(assignManId);
                for (let i = 0; i < data.length; i++) {
                    var opt="";
                    if(data[i].id==assignManId){
                        opt="<option value='"+data[i].id+"' selected>"+data[i].uname+"</option>";
                    }else{
                        opt="<option value='"+data[i].id+"'>"+data[i].uname+"</option>";
                    }
                    $("#assignMan").append(opt);
                }

            }
            layui.form.render("select");
        }
    })
    /**
     * 监听submit事件
     * 实现营销机会的添加与更新
     */
    form.on("submit(addOrUpdateSaleChance)", function (data) {
        // 提交数据时的加载层 （https://layer.layui.com/）
        var index = layer.msg("数据提交中,请稍后...",{
            icon:16, // 图标
            time:false, // 不关闭
            shade:0.8 // 设置遮罩的透明度
        });
        // 请求的地址
        var url = ctx + "/sale_chance/save";

        // 判断隐藏域中的ID是否为空，如果不为空则为修改操作
        if ($("input[name='id']").val()) {
            url = ctx + "/sale_chance/update";
        }
        // 发送ajax请求
        $.post(url, data.field, function (result) {
            console.log(data.field.id);
            console.log(data.field.uname);
            console.log(data.field.assignMan);
            console.log(data.field.customerName);
            // 操作成功
            if (result.code == 200) {
                // 提示成功
                layer.msg("操作成功！");
                // 关闭加载层
                layer.close(index);
                // 关闭弹出层
                layer.closeAll("iframe");
                // 刷新⽗⻚⾯，重新渲染表格数据
                parent.location.reload();
            } else {
                layer.msg(result.msg);
            }
        });
        return false; // 阻⽌表单提交
    });
});