package com.ai.opt.sso.client.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * FilterInvocation
 * Date: 2017年2月9日 <br>
 * Copyright (c) 2017 asiainfo.com <br>
 * 
 * @author
 */
public class FilterInvocation {
	private FilterChain chain;  
	private ServletRequest request;  
	private ServletResponse response;
	
	public FilterInvocation(ServletRequest request, ServletResponse response,
			FilterChain chain) {
		if ((request == null) || (response == null) || (chain == null)) {
			throw new IllegalArgumentException(
					"Cannot pass null values to constructor");
		}
		if (!(request instanceof HttpServletRequest)) {
			throw new IllegalArgumentException(
					"Can only process HttpServletRequest");
		}
		if (!(response instanceof HttpServletResponse)) {
			throw new IllegalArgumentException(
					"Can only process HttpServletResponse");
		}
		this.request = request;
		this.response = response;
		this.chain = chain;
	}
	
	/**
	 * 获取全部请求url
	 * @return
	 * @author
	 */
	public String getFullRequestUrl() {
		return this.getHttpRequest().getRequestURL().toString();
	}

	/**
	 * 获取请求url
	 * @return
	 * @author
	 */
	public String getRequestUrl() {
		return this.getHttpRequest().getRequestURI();
	}

	/**
	 * 获取请求
	 * @return
	 * @author
	 */
	public HttpServletRequest getHttpRequest() {
		return (HttpServletRequest) request;
	}

	/**
	 * 获取返回
	 * @return
	 * @author
	 */
	public HttpServletResponse getHttpResponse() {
		return (HttpServletResponse) response;
	}

	/**
	 * 获取链接
	 * @return
	 * @author
	 */
	public FilterChain getChain() {
		return chain;
	}
	/**
	 * 设置链接
	 * @param chain
	 * @author
	 */
	public void setChain(FilterChain chain) {
		this.chain = chain;
	}
	/**
	 * 获取请求
	 * @return
	 * @author
	 */
	public ServletRequest getRequest() {
		return request;
	}
	/**
	 * 设置请求
	 * @param request
	 * @author
	 */
	public void setRequest(ServletRequest request) {
		this.request = request;
	}
	/**
	 * 获取返回
	 * @return
	 * @author
	 */
	public ServletResponse getResponse() {
		return response;
	}
	/**
	 * 设置返回
	 * @param response
	 * @author
	 */
	public void setResponse(ServletResponse response) {
		this.response = response;
	}

}
