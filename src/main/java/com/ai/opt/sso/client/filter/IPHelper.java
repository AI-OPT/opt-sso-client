package com.ai.opt.sso.client.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * IP帮助类
 *
 * Date: 2016年3月2日 <br>
 * Copyright (c) 2016 asiainfo.com <br>
 * @author gucl
 */
public class IPHelper {

	/**
	 * 判断ipAddress是否为IP
	 * 
	 * @param ipAddress
	 * @return
	 * @author gucl
	 */
	public static boolean isIP(String ipAddress) {
		if (ipAddress.length() < 7 || ipAddress.length() > 15 || "".equals(ipAddress)) {
			return false;
		}
		/**
		 * 判断IP格式和范围
		 */
		String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

		Pattern pat = Pattern.compile(rexp);

		Matcher mat = pat.matcher(ipAddress);

		return mat.find();
	}
	/**
	 * 判断ipAddress是否为内网IP
	 * @param ipAddress
	 * @return
	 * @author gucl
	 */
	public static boolean isInnerIP(String ipAddress) {
		boolean isInnerIp = false;
		boolean isIPFlag = isIP(ipAddress);
		if (isIPFlag) {
			long ipNum = getIpNum(ipAddress);
			/**
			 * 私有IP：A类 10.0.0.0-10.255.255.255 B类 172.16.0.0-172.31.255.255 C类
			 * 192.168.0.0-192.168.255.255 当然，还有127这个网段是环回地址
			 **/
			long aBegin = getIpNum("10.0.0.0");
			long aEnd = getIpNum("10.255.255.255");
			long bBegin = getIpNum("172.16.0.0");
			long bEnd = getIpNum("172.31.255.255");
			long cBegin = getIpNum("192.168.0.0");
			long cEnd = getIpNum("192.168.255.255");
			isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
					|| ipAddress.equals("127.0.0.1");
		} else {
			if ("localhost".equalsIgnoreCase(ipAddress)) {
				isInnerIp = true;
			}
		}
		return isInnerIp;
	}

	private static long getIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);

		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}

}
