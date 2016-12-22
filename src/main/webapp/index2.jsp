<%@page import="java.util.Map"%>
<%@page import="org.jasig.cas.client.authentication.AttributePrincipal"%>
<%@page import="java.security.Principal"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>14105</title>
</head>
<body>
<%
Principal principal = request.getUserPrincipal();
if(principal!=null){
	out.println(principal.getName());
	AttributePrincipal ap = (AttributePrincipal)principal;
	Map<String,Object> attributes = ap.getAttributes();
	if(attributes!=null){
		for(String key:attributes.keySet()){
			out.println(key+":"+attributes.get(key));
		}
	}
}
%>
<%
if(principal!=null){
%>

<a href="<%=request.getContextPath() %>/logout.jsp">退出登录</a>

<%
}
%>




</body>
</html>