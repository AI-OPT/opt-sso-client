package com.ai.opt.sso.client.filter;

import java.io.Serializable;

/**
 * BaaS 项目 SSOUser 单点登录成功后的User实体
 *
 * Date: 2016年3月16日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * @author gucl
 */
public class SSOClientUser implements Serializable {
    private static final long serialVersionUID = -8147635836938729264L;

    //登录名称
    private String username;
    
    //账号ID
    private long accountId;
    //租户id
    private String tenantId;
    //账号名称
    private String accountName;
    //昵称
    private String nickName;
    //昵称缩写
    private String shortNickName;
    //手机号码
    private String phone;
    //邮件
    private String email;    
    //账号类型
    private String accountType;
    //账户级别
    private String accountLevel;
    //微信
    private String weixin;
    //微博
    private String weibo;
    //qq
    private String qq;    
    
    //以下字段单点登录服务端无法获取，需由各客户端通过查询业务获取，
    //行业类型（登录后各客户端查询业务表得出）
    private String industryCode;
    //行业名称（登录后各客户端查询业务表得出）
    private String industryName;
    //租户名称（登录后各客户端查询业务表得出）
    private String tenantName;
    
    
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getAccountLevel() {
		return accountLevel;
	}
	public void setAccountLevel(String accountLevel) {
		this.accountLevel = accountLevel;
	}
	public String getWeixin() {
		return weixin;
	}
	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}
	public String getWeibo() {
		return weibo;
	}
	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public String getIndustryCode() {
		return industryCode;
	}
	public void setIndustryCode(String industryCode) {
		this.industryCode = industryCode;
	}
	public String getIndustryName() {
		return industryName;
	}
	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
	public String getTenantName() {
		return tenantName;
	}
	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}
    public String getShortNickName() {
        return shortNickName;
    }
    public void setShortNickName(String shortNickName) {
        this.shortNickName = shortNickName;
    }
    

}
