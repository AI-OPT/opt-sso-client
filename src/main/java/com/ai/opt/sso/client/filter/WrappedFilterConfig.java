package com.ai.opt.sso.client.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class WrappedFilterConfig implements FilterConfig {
	private static final Logger LOG = Logger.getLogger(WrappedFilterConfig.class);
	private Map<String, String> params;
	
	private FilterConfig filterConfig;
	
	public WrappedFilterConfig(FilterConfig filterConfig){
		this.filterConfig = filterConfig;
		this.params = initParams();
	}

	@Override
	public String getFilterName() {
		return filterConfig.getFilterName();
	}

	@Override
	public String getInitParameter(String key) {
		String value = filterConfig.getInitParameter(key);
		if(value!=null){
			return value;
		}
		return params.get(key);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		final Iterator<String> iterator = params.keySet().iterator();
		return new Enumeration<String>() {

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public String nextElement() {
				return iterator.next();
			}
		};
	}

	@Override
	public ServletContext getServletContext() {
		return filterConfig.getServletContext();
	}
	
	private Map<String,String> initParams(){
		Properties properties = new Properties();
		Map<String, String> map = new HashMap<String, String>();
		try {
			ClassLoader loader = WrappedFilterConfig.class.getClassLoader();
			properties.load(loader.getResourceAsStream("sso.properties"));
			for (Object obj : properties.keySet()) {
				String key = (String) obj;
				if(key!=null){
					map.put(key.trim(), properties.getProperty(key).trim());
				}
			}
		} catch (IOException e) {
			LOG.error("init WrappedFilterConfig failure",e);
		}
		return map;
	}
	
}
