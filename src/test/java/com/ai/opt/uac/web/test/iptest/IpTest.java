package com.ai.opt.uac.web.test.iptest;

import com.ai.opt.sso.client.filter.IPHelper;

public class IpTest {

	public static void main(String[] args) {
		String [] list=new String[]{"yun.cicitdao.com"};
		System.out.println("-------"+IPHelper.isInnerIP("a.yun.cicitdao.com", list));
	}
}
