<%@page import="java.util.Map"%>
<%@page import="org.jasig.cas.client.authentication.AttributePrincipal"%>
<%@page import="java.security.Principal"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录成功</title>
</head>
<%--<body onload="document.getElementById('myForm').submit()">--%>
<body>
<form id="myForm" method="post" action="http://www.baidu.com">
<%
Principal principal = request.getUserPrincipal();
if(principal!=null){
	out.println("欢迎你，"+principal.getName());
	out.println("<a href='"+request.getContextPath()+"/ssologout'>退出登录</a><br/>");
	AttributePrincipal ap = (AttributePrincipal)principal;
	Map<String,Object> attributes = ap.getAttributes();
	if(attributes!=null){
		for(String key:attributes.keySet()){
			out.println(key+":"+attributes.get(key)+"<br/>");
		}
	}
	out.println("<input type=\"hidden\" name=\"userId\" value='"+attributes.get("userId")+"'/>");
	out.println("<input type=\"hidden\" name=\"nickname\" value='"+attributes.get("nickname")+"'/>");
	out.println("<input type=\"hidden\" name=\"username\" value='"+attributes.get("username")+"'/>");
}
%>
</form>
</body>
</html>