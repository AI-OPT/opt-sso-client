package com.ai.opt.sso.client.filter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SSO配置文件工具类
 *
 * Date: 2015年11月29日 <br>
 * Copyright (c) 2015 asiainfo.com <br>
 * 
 * @author gucl
 */
public final class SSOClientUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SSOClientUtil.class);
    
    

    //sso.properties属性文件
    private static Properties prop = new Properties();
    
    //内网域名
    private static String[] innerDomains;
    
    public static Properties getProp() {
		return prop;
	}
	public static void setProp(Properties prop) {
		SSOClientUtil.prop = prop;
	}
	public static String[] getInnerDomains() {
		return innerDomains;
	}
	public static void setInnerDomains(String[] innerDomains) {
		SSOClientUtil.innerDomains = innerDomains;
	}
	private SSOClientUtil() {
    }

    static {
        try {

            InputStream inStream = SSOClientUtil.class.getClassLoader().getResourceAsStream(
                    "sso.properties");
            prop.load(inStream);
            String strInnerDomains=prop.getProperty("innerDomains","");
            if(!"".equalsIgnoreCase(strInnerDomains.trim())){
            	innerDomains=strInnerDomains.split(",");
            }
            LOG.debug("加载sso.properties完毕");
        } catch (Exception e) {
            LOG.debug("加载sso.properties失败，具体原因：" + e.getMessage(), e);
//            throw new SystemException("加载sso.properties失败", e);
        }
    }

    /**
     * 单点登录服务器登录地址(外网)
     * 
     * @return
     * @author gucl
     */
    public static String getCasServerLoginUrl() {
        return prop.getProperty("casServerLoginUrl", "").trim();
    }
    /**
     * 单点登录服务器登录地址(内网)
     * @return
     * @author gucl
     */
    public static String getCasServerLoginUrlInner() {
    	return prop.getProperty("casServerLoginUrl_Inner", "").trim();
    }
    /**
     * 单点登录服务器登录地址(自动判断内外网)
     * @param request
     * @return
     * @author gucl
     */
    public static String getCasServerLoginUrlRuntime(HttpServletRequest request) {
    	String serverName=request.getServerName();
		boolean innerFlag=IPHelper.isInnerIP(serverName,innerDomains);
		if(!innerFlag){
			return prop.getProperty("casServerLoginUrl", "").trim();
		}
		else{
			return prop.getProperty("casServerLoginUrl_Inner", "").trim();
		}
    }

    /**
     * 单点登录服务器主机地址(外网)
     * @return
     * @author gucl
     */
    public static String getCasServerUrlPrefix() {
    	return prop.getProperty("casServerUrlPrefix", "").trim();
    }
    
    /**
     * 单点登录服务器主机地址(内网)
     * @return
     * @author gucl
     */
    public static String getCasServerUrlPrefixInner() {
        return prop.getProperty("casServerUrlPrefix_Inner", "").trim();
    }
    /**
     * 单点登录服务器主机地址(自动判断内外网)
     * @param request
     * @return
     * @author gucl
     */
    public static String getCasServerUrlPrefixRuntime(HttpServletRequest request) {
    	String serverName=request.getServerName();
		boolean innerFlag=IPHelper.isInnerIP(serverName,innerDomains);
		if(!innerFlag){
			return prop.getProperty("casServerUrlPrefix", "").trim();
		}
		else{
			return prop.getProperty("casServerUrlPrefix_Inner", "").trim();
		}
    }

    /**
     * 单点登录客户端主机地址(外网)
     * @return
     * @author gucl
     */
    public static String getServerName() {
        return prop.getProperty("serverName", "").trim();
    }
    /**
     * 单点登录客户端主机地址(内网)
     * @return
     * @author gucl
     */
    public static String getServerNameInner() {
    	return prop.getProperty("serverName_Inner", "").trim();
    }
    /**
     * 单点登录客户端主机地址(自动判断内外网)
     * @param request
     * @return
     * @author gucl
     */
    public static String getServerNameRuntime(HttpServletRequest request) {
    	String serverName=request.getServerName();
		boolean innerFlag=IPHelper.isInnerIP(serverName,innerDomains);
		if(!innerFlag){
			return prop.getProperty("serverName", "").trim();
		}
		else{
			return prop.getProperty("serverName_Inner", "").trim();
		}
    }
    /**
     * 编码
     * @return
     * @author gucl
     */
    public static String getEncoding() {
        return prop.getProperty("encoding", "UTF-8").trim();
    }
    /**
     * 单点登录登出地址(外网)
     * @return
     * @author gucl
     */
    public static String getLogOutServerUrl() {
        return prop.getProperty("logOutServerUrl", "").trim();
    }
    /**
     * 单点登录登出地址(内网)
     * @return
     * @author gucl
     */
    public static String getLogOutServerUrlInner() {
    	return prop.getProperty("logOutServerUrl_Inner", "").trim();
    }
    /**
     * 单点登录登出地址(自动判断内外网)
     * @param request
     * @return
     * @author gucl
     */
    public static String getLogOutServerUrlRuntime(HttpServletRequest request) {
    	String serverName=request.getServerName();
		boolean innerFlag=IPHelper.isInnerIP(serverName,innerDomains);
		if(!innerFlag){
			return prop.getProperty("logOutServerUrl", "").trim();
		}
		else{
			return prop.getProperty("logOutServerUrl_Inner", "").trim();
		}
    }

    /**
     * 单点登录登出后返回地址(外网)
     * @return
     * @author gucl
     */
    public static String getLogOutBackUrl() {
        return prop.getProperty("logOutBackUrl", "").trim();
    }
    /**
     * 单点登录登出后返回地址(内网)
     * @return
     * @author gucl
     */
    public static String getLogOutBackUrlInner() {
    	return prop.getProperty("logOutBackUrl_Inner", "").trim();
    }
    /**
     * 获取用户语言的URL参数名
     * @return
     * @author jackieliu
     * @ApiDocMethod
     */
    public static String getLocaleParamName(){
        return prop.getProperty(SSOClientConstants.LOCALE_PARAM_NAME,"").trim();
    }
    /**
     * 获取用户语言的cookie名
     * @return
     * @author jackieliu
     * @ApiDocMethod
     */
    public static String getLocaleCookieName(){
        return prop.getProperty(SSOClientConstants.LOCALE_COOKIE_NAME,"").trim();
    }
    /**
     * 单点登录登出后返回地址(自动判断内外网)
     * @param request
     * @return
     * @author gucl
     */
    public static String getLogOutBackUrlRuntime(HttpServletRequest request) {
    	String serverName=request.getServerName();
		boolean innerFlag=IPHelper.isInnerIP(serverName,innerDomains);
		if(!innerFlag){
			return prop.getProperty("logOutBackUrl", "").trim();
		}
		else{
			return prop.getProperty("logOutBackUrl_Inner", "").trim();
		}
    }

    public static String getSystemId(){
        return prop.getProperty(SSOClientConstants.SYSTEM_ID,"").trim();
    }

    public static String getProperty(String key) {
        return prop.getProperty(key, "").trim();
    }

}
