package com.ai.opt.sso.client.filter;

import java.io.Serializable;

/**
 * SLP 项目 SSOUser 单点登录成功后的User实体
 *
 * Date: 2016年3月16日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * @author gucl
 */
public class SLPClientUser implements Serializable {
    private static final long serialVersionUID = -8147635836938729264L;

    //登录名称
    private String username;
    
    //租户id
    private String tenantId;
    //账号ID
    private String userId;
    /**用户类型:
     * 10：个人
	 * 11：企业
	 * 12：代理商
	 * 13：供货商
     */
    private String userType;
    //用户flag
    private String userFlag;
    //账号名称
    private String userLoginName;
    //昵称
    private String userNickname;
    //昵称缩写
    private String shortUserNickName;
    
    
    private String userState;

    private String vipLevel;

    private String safetyLevel;

    private String pwdSafetyLevel;
    //手机号码
    private String userMp;
    //邮件
    private String userEmail;

    private String emailValidateFlag;
    
    private String provinceCode;

    private String cityCode;

    private String registerWay;

    private String registerSource;

    private String verifyFlag;

    private String creditFlag;

    //以下字段单点登录服务端无法获取，需由各客户端通过查询业务获取，
    //行业类型（登录后各客户端查询业务表得出）
    private String industryCode;
    //行业名称（登录后各客户端查询业务表得出）
    private String industryName;
    //租户名称（登录后各客户端查询业务表得出）
    private String tenantName;
    //账管账户ID
    private long acctId;
    
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
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserFlag() {
		return userFlag;
	}
	public void setUserFlag(String userFlag) {
		this.userFlag = userFlag;
	}
	public String getUserLoginName() {
		return userLoginName;
	}
	public void setUserLoginName(String userLoginName) {
		this.userLoginName = userLoginName;
	}
	public String getUserNickname() {
		return userNickname;
	}
	public void setUserNickname(String userNickname) {
		this.userNickname = userNickname;
	}
	public String getShortUserNickName() {
		return shortUserNickName;
	}
	public void setShortUserNickName(String shortUserNickName) {
		this.shortUserNickName = shortUserNickName;
	}
	public String getUserState() {
		return userState;
	}
	public void setUserState(String userState) {
		this.userState = userState;
	}
	public String getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(String vipLevel) {
		this.vipLevel = vipLevel;
	}
	public String getSafetyLevel() {
		return safetyLevel;
	}
	public void setSafetyLevel(String safetyLevel) {
		this.safetyLevel = safetyLevel;
	}
	public String getPwdSafetyLevel() {
		return pwdSafetyLevel;
	}
	public void setPwdSafetyLevel(String pwdSafetyLevel) {
		this.pwdSafetyLevel = pwdSafetyLevel;
	}
	public String getUserMp() {
		return userMp;
	}
	public void setUserMp(String userMp) {
		this.userMp = userMp;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getEmailValidateFlag() {
		return emailValidateFlag;
	}
	public void setEmailValidateFlag(String emailValidateFlag) {
		this.emailValidateFlag = emailValidateFlag;
	}
	public String getProvinceCode() {
		return provinceCode;
	}
	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getRegisterWay() {
		return registerWay;
	}
	public void setRegisterWay(String registerWay) {
		this.registerWay = registerWay;
	}
	public String getRegisterSource() {
		return registerSource;
	}
	public void setRegisterSource(String registerSource) {
		this.registerSource = registerSource;
	}
	public String getVerifyFlag() {
		return verifyFlag;
	}
	public void setVerifyFlag(String verifyFlag) {
		this.verifyFlag = verifyFlag;
	}
	public String getCreditFlag() {
		return creditFlag;
	}
	public void setCreditFlag(String creditFlag) {
		this.creditFlag = creditFlag;
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
	public long getAcctId() {
		return acctId;
	}
	public void setAcctId(long acctId) {
		this.acctId = acctId;
	}
    
	
	public static final class USERTYPE{
		private USERTYPE(){};
		public static final String PERSONAL="10";
		public static final String COMPANY="11";
		public static final String AGENT="12";
		public static final String SUPPLIER="13";
	}

}
