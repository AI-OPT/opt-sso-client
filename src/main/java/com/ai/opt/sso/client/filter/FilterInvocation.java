package com.ai.opt.sso.client.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	
	public String getFullRequestUrl() {
		return this.getHttpRequest().getRequestURL().toString();
	}

	public String getRequestUrl() {
		return this.getHttpRequest().getRequestURI();
	}

	public HttpServletRequest getHttpRequest() {
		return (HttpServletRequest) request;
	}

	public HttpServletResponse getHttpResponse() {
		return (HttpServletResponse) response;
	}

	public FilterChain getChain() {
		return chain;
	}
	public void setChain(FilterChain chain) {
		this.chain = chain;
	}
	public ServletRequest getRequest() {
		return request;
	}
	public void setRequest(ServletRequest request) {
		this.request = request;
	}
	public ServletResponse getResponse() {
		return response;
	}
	public void setResponse(ServletResponse response) {
		this.response = response;
	}

}
