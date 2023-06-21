layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    form.on('submit(login)',function (data){
        console.log(data.field);
        $.ajax({
            type:"post",
            url:ctx+"/user/login",
            data:{
                userName:data.field.username,
                userPwd:data.field.password
            },
            success:function (data){
                console.log(data);
                if(data.code==200){
                    layer.msg("登录成功",function (){
                        var result=data.result;
                        // 如果⽤户选择"记住我"，则设置cookie的有效期为7天
                        if($("input[type='checkbox']").is(":checked")){
                            $.cookie("userIdStr", result.userIdStr, { expires: 7 });
                            $.cookie("userName", result.userName, { expires: 7 });
                            $.cookie("trueName", result.trueName, { expires: 7 });
                        }
                        else{
                            console.log(result.userName+result.trueName+result.userIdStr);
                            $.cookie("userName", result.userName);
                            $.cookie("trueName", result.trueName);
                            $.cookie("userIdStr", result.userIdStr);
                        }
                        window.location.href=ctx+"/main";
                    })
                }else{
                    layer.msg(data.msg,{icon:5});
                }
            }
        })
        return false;
    })
});
