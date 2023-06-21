layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    form.on('submit(saveBtn)',function (data){
        console.log(data.field);
        $.ajax({
            type:"post",
            url:ctx+"/user/update",
            data:{
                oldPassword:data.field.old_password,
                newPassword:data.field.new_password,
                confirmPassword:data.field.again_password
            },
            success:function (result){
                console.log(result);
                if(result.code==200){
                    layer.msg("修改成功，用户将在3秒后退出。。。。",function (){
                        var result1=result.result;
                        $.removeCookie("userIdStr", {domin:"localhost",path:"/crm"});
                        $.removeCookie("userName", {domin:"localhost",path:"/crm"});
                        $.removeCookie("trueName", {domin:"localhost",path:"/crm"});
                    })
                    window.parent.location.href=ctx+"/index";
                }else{
                    layer.msg(result.msg,{icon:5});
                }
            }
        })
        return false;
    })
});
