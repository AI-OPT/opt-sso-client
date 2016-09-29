package com.ai.opt.sso.client.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.util.AbstractConfigurationFilter;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FilterChainProxy extends AbstractConfigurationFilter {

	Logger LOG = LoggerFactory.getLogger(getClass());
	
	private Filter[] ssofilters;
	private  String[] ignore_resources;
	private Map<String, List<Filter>> filterlistMap;
	private FilterConfig currentFilterConfig;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.currentFilterConfig=filterConfig;
		String exclude = currentFilterConfig.getInitParameter("ignore_resources");
		if(exclude!=null){
			ignore_resources = exclude.split(",");
		}
	}

	private Map<String, List<Filter>> ObtainAllDefinedFilters() {
		Map<String, List<Filter>> listmap = new HashMap<String, List<Filter>>();
		List<Filter> ssolist = new ArrayList<Filter>();
		
		ssolist.add(new SingleSignOutFilter());  
		ssolist.add(new CustomAuthenticationFilter());  
		ssolist.add(new Cas20ProxyReceivingTicketValidationFilter());  
		ssolist.add(new AssertionThreadLocalFilter());  
		ssolist.add(new HttpServletRequestWrapperFilter());
		
		listmap.put("sso", ssolist);
		
		return listmap;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String currentResource =  req.getRequestURI();
		
		/////初始化过滤器链的参数--- 开始//////
		WrappedFilterConfig wrappedFilterConfig = new WrappedFilterConfig(currentFilterConfig,req);
		filterlistMap = ObtainAllDefinedFilters();
		for (List<Filter> list : filterlistMap.values()) {
			for(Filter filter : list){
				if(filter!=null){
					if(LOG.isDebugEnabled()){
						 LOG.debug("Initializing Filter defined in ApplicationContext: '" + filter.toString() + "'");
					}
					filter.init(wrappedFilterConfig);
				}
			}
		}
		ssofilters = filterlistMap.get("sso").toArray(new Filter[0]);
		
	    /////初始化过滤器链的参数--- 结束//////
		
		
		FilterInvocation fi = new FilterInvocation(request, response, chain);
		if (filterlistMap.size() == 0) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(fi.getRequestUrl() + " has an empty filter list");
			}
			chain.doFilter(request, response);
			return;
		}
		
		
		if(currentResource!=null&&!isIgnored(currentResource.toLowerCase())){
			VirtualFilterChain virtualFilterChain = new VirtualFilterChain(fi,this.ssofilters);
			virtualFilterChain.doFilter(fi.getRequest(), fi.getResponse());
		}else{
			chain.doFilter(req, response);
		}
	}  

	@Override
	public void destroy() {
		if(filterlistMap!=null){
			for (List<Filter> list : filterlistMap.values()) {
				for(Filter filter : list){
					if(filter!=null){
						if(LOG.isDebugEnabled()){
							LOG.debug("Destroying Filter defined in ApplicationContext: '" + filter.toString() + "'");
						}
						filter.destroy();
					}
				}
			}
		}
	}
	
	private class VirtualFilterChain implements FilterChain {
		private FilterInvocation fi;
		private Filter[] additionalFilters;
		private int currentPosition = 0;

		public VirtualFilterChain(FilterInvocation filterInvocation,
				Filter[] additionalFilters) {
			this.fi = filterInvocation;
			this.additionalFilters = additionalFilters;
		}

		public void doFilter(ServletRequest request, ServletResponse response)
				throws IOException, ServletException {
			if (currentPosition == additionalFilters.length) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(fi.getRequestUrl()
							+ " reached end of additional filter chain; proceeding with original chain");
				}
				fi.getChain().doFilter(request, response);
			} else {
				currentPosition++;
				if (LOG.isDebugEnabled()) {
					LOG.debug(fi.getRequestUrl() + " at position "
							+ currentPosition + " of "
							+ additionalFilters.length
							+ " in additional filter chain; firing Filter: '"
							+ additionalFilters[currentPosition - 1] + "'");
				}
				additionalFilters[currentPosition - 1].doFilter(request,
						response, this);
			}
		}
	}

	private  boolean isIgnored(String requestUrl) {
		if (ignore_resources == null){
			return false;
		}else{
			for (String suffix : ignore_resources) {
				if (!StringUtils.isBlank(suffix)&&requestUrl.endsWith(suffix.trim().toLowerCase())) {
					return true;
				}
			} 
			return false;
		}
	}

}

