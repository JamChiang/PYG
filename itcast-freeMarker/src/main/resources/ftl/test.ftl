<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>freeMarker测试</title>
</head>
<body>

<h1>${name}:${message}</h1>

<br><hr><br>
<#assign linkName="巨头">
联系人:${linkName}
<br>
<#assign info={"phone":"1234567","price":"123"}>
${info.phone}:${info.price}
<br><hr><br>
<#assign boole=true>
<#if boole>
    boole = true
<#else>
    boole = false
</#if>
<br><hr><br>
<#assign str="{'id':123,'price':321}">
<#assign json=str?eval>
id:${json.id},price:${json.price}
<br><hr><br>
当前日期:${today?date}<br>
当前时间:${today?time}<br>
日期+时间:${today?datetime}<br>

特定格式:${today?string("yyyy年MM月dd日 HH:mm:ss")}<br>
<br><hr><br>
${number}.......${number?c}
<br><hr><br>
<br><hr><br>
<br><hr><br>

</body>
</html>