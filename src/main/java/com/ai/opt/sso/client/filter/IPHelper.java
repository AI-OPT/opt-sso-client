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
	 * 将127.0.0.1形式的IP地址转换成十进制整数
	 * @param strIp
	 * @return
	 * @author gucl
	 */
	public static long ipToLong(String strIp) {
		long[] ip = new long[4];
		// 先找到IP地址字符串中.的位置
		int position1 = strIp.indexOf(".");
		int position2 = strIp.indexOf(".", position1 + 1);
		int position3 = strIp.indexOf(".", position2 + 1);
		// 将每个.之间的字符串转换成整型
		ip[0] = Long.parseLong(strIp.substring(0, position1));
		ip[1] = Long.parseLong(strIp.substring(position1 + 1, position2));
		ip[2] = Long.parseLong(strIp.substring(position2 + 1, position3));
		ip[3] = Long.parseLong(strIp.substring(position3 + 1));
		return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
	}

	/**
	 * 将十进制整数形式转换成127.0.0.1形式的ip地址
	 * @param longIp
	 * @return
	 * @author gucl
	 */
	public static String longToIP(long longIp) {
		StringBuffer sb = new StringBuffer("");
		// 直接右移24位
		sb.append(String.valueOf((longIp >>> 24)));
		sb.append(".");
		// 将高8位置0，然后右移16位
		sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
		sb.append(".");
		// 将高16位置0，然后右移8位
		sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
		sb.append(".");
		// 将高24位置0
		sb.append(String.valueOf((longIp & 0x000000FF)));
		return sb.toString();
	}

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
		return isInnerIP(ipAddress,null);
	}
	
	public static boolean isInnerIP(String ipAddress,String[] innerDomains) {
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
			if(innerDomains!=null&&innerDomains.length>0){
				for(String domain:innerDomains){
					if(ipAddress.contains(domain)){
						isInnerIp=true;
						break;
					}
				}
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
