package com.ai.opt.sso.client.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

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
	private ThreadLocal<Map<String, List<Filter>>> filterlistMap=new ThreadLocal<Map<String, List<Filter>>>();
	private Map<String, String> params=new ConcurrentHashMap<String, String>();
	private ThreadLocal<Map<String, String>> threadParams=new ThreadLocal<Map<String, String>>();
	private FilterConfig currentFilterConfig;
	
	/**
	 * 初始化过滤器
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.currentFilterConfig=filterConfig;
		//获取忽略列表
		String exclude = currentFilterConfig.getInitParameter("ignore_resources");
		if(exclude!=null){
			ignore_resources = exclude.split(",");
		}
		initParams();
		
	}
	/**
	 * 读取sso.properties初始化参数（params属性）
	 * 
	 * @author jackieliu
	 * @ApiDocMethod
	 */
	private void initParams(){
		//jvm里如果有map，则直接返回
		if(!params.isEmpty()){
			return;
		}
		//jvm里如果没有map，则读取sso.properties文件
		else{
			//同步加锁
			synchronized (FilterChainProxy.class) {
				//加锁后，还没有的话，则读取sso.properties文件，否则说明其他线程已加载，无需重复加载
				if(params.isEmpty()){
					Properties properties = new Properties();		
					try {
						ClassLoader loader = WrappedFilterConfig.class.getClassLoader();
						properties.load(loader.getResourceAsStream("sso.properties"));
						for (Object obj : properties.keySet()) {
							String key = (String) obj;
							if(key!=null){
								params.put(key.trim(), properties.getProperty(key).trim());
							}
						}
					} catch (IOException e) {
						LOG.error("init WrappedFilterConfig failure",e);
					}
						
					
				}
			}// end synchronized
			
		}//end else
	}
	
	/**
	 * 初始化过滤器链
	 * @return
	 * @author jackieliu
	 * @ApiDocMethod
	 */
	private ThreadLocal<Map<String, List<Filter>>> ObtainAllDefinedFilters() {
		Map<String, List<Filter>> listmap = new HashMap<String, List<Filter>>();
		List<Filter> ssolist = new ArrayList<Filter>();
		//cas的单点登出
		ssolist.add(new SingleSignOutFilter());  
		ssolist.add(new CustomAuthenticationFilter());  
		ssolist.add(new Cas20ProxyReceivingTicketValidationFilter());  
		ssolist.add(new AssertionThreadLocalFilter());  
		ssolist.add(new HttpServletRequestWrapperFilter());
		
		listmap.put("sso", ssolist);
		filterlistMap.set(listmap);
		return filterlistMap;
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String currentResource =  req.getRequestURI();
		/////初始化过滤器链的参数--- 开始//////
		filterlistMap = ObtainAllDefinedFilters();
		Map<String, String> currParams=new ConcurrentHashMap<String, String>();
		HttpServletRequest httpRequest=(HttpServletRequest)request;
		String serverName=httpRequest.getServerName();
		boolean innerFlag=IPHelper.isInnerIP(serverName,SSOClientUtil.getInnerDomains());
		//若是内网访问，则单点登录走内网
		//深度拷贝params到currParms
		Iterator iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next();
			String key = entry.getKey();
			String val = entry.getValue();
			currParams.put(key, val);
			
		}
		
		if(innerFlag){
			//若是内网访问，则单点登录走内网
			Iterator iterInner = currParams.entrySet().iterator();
				while (iterInner.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) iterInner.next();
					String key = entry.getKey();
					if(null!=key&&!"".equals(key)&&key.endsWith("_Inner")){
						String val = entry.getValue();
						String keyNormal=key.replace("_Inner", "");
						currParams.put(keyNormal, val);
					}	
					
				}
		}
		
		threadParams.set(currParams);
		
		
		WrappedFilterConfig wrappedFilterConfig = new WrappedFilterConfig(currentFilterConfig,threadParams);
		
		for (List<Filter> list : filterlistMap.get().values()) {
			for(Filter filter : list){
				if(filter!=null){
					if(LOG.isDebugEnabled()){
						 LOG.debug("Initializing Filter defined in ApplicationContext: '" + filter.toString() + "'");
					}
					filter.init(wrappedFilterConfig);
				}
			}
		}
		ssofilters = filterlistMap.get().get("sso").toArray(new Filter[0]);
		
	    /////初始化过滤器链的参数--- 结束//////
		
		
		FilterInvocation fi = new FilterInvocation(request, response, chain);
		if (filterlistMap.get().size() == 0) {
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
		if(filterlistMap!=null&&filterlistMap.get()!=null){
			for (List<Filter> list : filterlistMap.get().values()) {
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

