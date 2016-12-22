<%@page import="java.io.IOException"%>
<%@page import="com.ai.opt.sso.client.filter.SSOClientConstants"%>
<%@page import="com.ai.opt.sso.client.filter.SSOClientUtil"%>
<%@page import="com.ai.opt.sso.client.filter.SSOClientUser"%>
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
SSOClientUser user = (SSOClientUser) session.getAttribute(SSOClientConstants.USER_SESSION_KEY);
String logOutServerUrl = SSOClientUtil.getLogOutServerUrlRuntime(request);
String logOutBackUrl = SSOClientUtil.getLogOutBackUrlRuntime(request);
try {
	if(user!=null){
			//session.removeAttribute(SSOClientConstants.USER_SESSION_KEY);
			session.invalidate();
	}
	System.out.println("【logout.jsp】logOutServerUrl="+logOutServerUrl);
	response.sendRedirect(logOutServerUrl + "?service=" + logOutBackUrl);
} catch (IOException e) {
	System.out.println("用户登出失败");
}
%>





</body>
</html>