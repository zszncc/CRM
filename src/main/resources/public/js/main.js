layui.use(['element', 'layer', 'layuimini','jquery','jquery_cookie'], function () {
    var $ = layui.jquery,
        layer = layui.layer,
        $ = layui.jquery_cookie($);

    // 菜单初始化
    $('#layuiminiHomeTabIframe').html('<iframe width="100%" height="100%" frameborder="0"  src="welcome"></iframe>')
    layuimini.initTab();

    $(".login-out").click(function (){
       $.removeCookie("userIdStr",{domin:"localhost",path:"/crm"});
       $.removeCookie("userName",{domin:"localhost",path:"/crm"});
       $.removeCookie("trueName",{domin:"localhost",path:"/crm"});
       window.parent.location.href=ctx+"/index";
    });

});