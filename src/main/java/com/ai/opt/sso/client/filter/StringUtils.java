package com.ai.opt.sso.client.filter;

/**
 * 字符串处理工具类
 * Date: 2016年12月16日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * 
 * @author jackieliu
 */
public final class StringUtils {
    private StringUtils(){}
    
    public static boolean isBlank(String str) {
        if (null == str || "".equals(str.trim()) || str.trim().length()<1) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
