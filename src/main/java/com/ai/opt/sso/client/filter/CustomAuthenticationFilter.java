package com.ai.opt.sso.client.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;
import org.jasig.cas.client.authentication.ContainsPatternUrlPatternMatcherStrategy;
import org.jasig.cas.client.authentication.DefaultAuthenticationRedirectStrategy;
import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.ExactUrlPatternMatcherStrategy;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.authentication.RegexUrlPatternMatcherStrategy;
import org.jasig.cas.client.authentication.UrlPatternMatcherStrategy;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.util.ReflectUtils;
import org.jasig.cas.client.validation.Assertion;

public class CustomAuthenticationFilter extends AbstractCasFilter {
	
	/**
	 * 登录类型
	 */
	//private String loginType;
	/**
	 * 服务端登出url
	 */
	private String logOutServerUrl;
	/**
	 * 登出返回url
	 */
	private String logOutBackUrl;
	
    private String casServerLoginUrl;
    /*
     本系统标识，用于单点登录服务端识别系统。
     */
    private String systemId;
    
    private boolean renew = false;

    private boolean gateway = false;
    
    private String localeParamName;
    
    private String localeCookieName;

    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

    private AuthenticationRedirectStrategy authenticationRedirectStrategy = new DefaultAuthenticationRedirectStrategy();
    
    private UrlPatternMatcherStrategy ignoreUrlPatternMatcherStrategyClass = null;
    
    private static final Map<String, Class<? extends UrlPatternMatcherStrategy>> PATTERN_MATCHER_TYPES =
            new HashMap<String, Class<? extends UrlPatternMatcherStrategy>>();
    
    static {
        PATTERN_MATCHER_TYPES.put("CONTAINS", ContainsPatternUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES.put("REGEX", RegexUrlPatternMatcherStrategy.class);
        PATTERN_MATCHER_TYPES.put("EXACT", ExactUrlPatternMatcherStrategy.class);
    }
    
    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            setCasServerLoginUrl(getPropertyFromInitParams(filterConfig, "casServerLoginUrl", null));
            logger.trace("Loaded CasServerLoginUrl parameter: {}", this.casServerLoginUrl);
            setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
            logger.trace("Loaded renew parameter: {}", this.renew);
            setGateway(parseBoolean(getPropertyFromInitParams(filterConfig, "gateway", "false")));
            logger.trace("Loaded gateway parameter: {}", this.gateway);
//            setLoginType(getPropertyFromInitParams(filterConfig, "loginType", null));
//            logger.trace("Loaded loginType parameter: {}", this.loginType);
            setLogOutServerUrl(getPropertyFromInitParams(filterConfig, "logOutServerUrl", null));
            logger.trace("Loaded logOutServerUrl parameter: {}", this.logOutServerUrl);
            setLogOutBackUrl(getPropertyFromInitParams(filterConfig, "logOutBackUrl", null));
            logger.trace("Loaded logOutBackUrl parameter: {}", this.logOutBackUrl);
            
            setLocaleParamName(getPropertyFromInitParams(filterConfig,SSOClientConstants.LOCALE_PARAM_NAME,null));
            logger.trace("Loaded localeParamName parameter: {}", this.localeParamName);
            setLocaleCookieName(getPropertyFromInitParams(filterConfig,SSOClientConstants.LOCALE_COOKIE_NAME,null));
            logger.trace("Loaded localeCookieName parameter: {}", this.localeCookieName);

            setSystemId(getPropertyFromInitParams(filterConfig,SSOClientConstants.SYSTEM_ID,null));
            logger.trace("Loaded localeParamName parameter: {}", this.systemId);
            
            final String ignorePattern = getPropertyFromInitParams(filterConfig, "ignorePattern", null);
            logger.trace("Loaded ignorePattern parameter: {}", ignorePattern);
            
            final String ignoreUrlPatternType = getPropertyFromInitParams(filterConfig, "ignoreUrlPatternType", "REGEX");
            logger.trace("Loaded ignoreUrlPatternType parameter: {}", ignoreUrlPatternType);
            
            if (ignorePattern != null) {
                final Class<? extends UrlPatternMatcherStrategy> ignoreUrlMatcherClass = PATTERN_MATCHER_TYPES.get(ignoreUrlPatternType);
                if (ignoreUrlMatcherClass != null) {
                    this.ignoreUrlPatternMatcherStrategyClass = ReflectUtils.newInstance(ignoreUrlMatcherClass.getName());
                } else {
                    try {
                        logger.trace("Assuming {} is a qualified class name...", ignoreUrlPatternType);
                        this.ignoreUrlPatternMatcherStrategyClass = ReflectUtils.newInstance(ignoreUrlPatternType);
                    } catch (final IllegalArgumentException e) {
                        logger.error("Could not instantiate class [{}]", ignoreUrlPatternType, e);
                    }
                }
                if (this.ignoreUrlPatternMatcherStrategyClass != null) {
                    this.ignoreUrlPatternMatcherStrategyClass.setPattern(ignorePattern);
                }
            }
            
            final String gatewayStorageClass = getPropertyFromInitParams(filterConfig, "gatewayStorageClass", null);

            if (gatewayStorageClass != null) {
                this.gatewayStorage = ReflectUtils.newInstance(gatewayStorageClass);
            }
            
            final String authenticationRedirectStrategyClass = getPropertyFromInitParams(filterConfig,
                    "authenticationRedirectStrategyClass", null);

            if (authenticationRedirectStrategyClass != null) {
                this.authenticationRedirectStrategy = ReflectUtils.newInstance(authenticationRedirectStrategyClass);
            }
        }
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.casServerLoginUrl, "casServerLoginUrl cannot be null.");
    }
    
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        if (isRequestUrlExcluded(request)) {
            logger.debug("Request is ignored.");
            filterChain.doFilter(request, response);
            return;
        }
        
        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;
        if (assertion != null) {
            filterChain.doFilter(request, response);
            return;
        }

        final String serviceUrl = constructServiceUrl(request, response);
        final String ticket = retrieveTicketFromRequest(request);
        final boolean wasGatewayed = this.gateway && this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

        if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
            filterChain.doFilter(request, response);
            return;
        }

        final String modifiedServiceUrl;

        logger.debug("no ticket and no assertion found");
        if (this.gateway) {
            logger.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }

        logger.debug("Constructed service url: {}", modifiedServiceUrl);

        final String urlToRedirectTo = constructRedirectUrl(this.casServerLoginUrl,getLocale(request),
                getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);

        logger.debug("redirecting to \"{}\"", urlToRedirectTo);
        this.authenticationRedirectStrategy.redirect(request, response, urlToRedirectTo);
	}

	/**
	 * 产生重定向地址
	 * @param casLoginUrl cas登录地址
	 * @param locale 当前所用语言，如en_US,zh_CN
	 * @param serviceParameterName
	 * @param serviceUrl
	 * @param isrenew
	 * @param isgateway
	 * @return
	 * @author jackieliu
	 * @ApiDocMethod
	 */
	protected String constructRedirectUrl(String casLoginUrl,String locale,
			String serviceParameterName, String serviceUrl, boolean isrenew,
			boolean isgateway) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(casLoginUrl)
		.append((casLoginUrl.contains("?")) ? "&" : "?")
		.append(serviceParameterName).append("=")
		.append(CommonUtils.urlEncode(serviceUrl))
		.append((isrenew) ? "&renew=true" : "")
		.append((isgateway) ? "&gateway=true" : "");
		if(locale!=null && locale.trim().length()>0){
		    buffer.append("&locale=").append(locale);
		}
		if (this.systemId!=null && this.systemId.trim().length()>0){
		    buffer.append("&systemId=").append(this.systemId);
        }
		
		return buffer.toString();
	}

	public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
        this.gatewayStorage = gatewayStorage;
    }
        
    private boolean isRequestUrlExcluded(final HttpServletRequest request) {
        if (this.ignoreUrlPatternMatcherStrategyClass == null) {
            return false;
        }
        
        final StringBuffer urlBuffer = request.getRequestURL();
        if (request.getQueryString() != null) {
            urlBuffer.append("?").append(request.getQueryString());
        }
        final String requestUri = urlBuffer.toString();
        return this.ignoreUrlPatternMatcherStrategyClass.matches(requestUri);
    }
    /**
     * 获取当前所用语言
     * @param request
     * @return
     * @author jackieliu
     * @ApiDocMethod
     */
    private String getLocale(HttpServletRequest request){
        String localeStr = null;
        //获取地址中参数
        if(StringUtils.isNotBlank(this.localeParamName)){
            localeStr = request.getParameter(this.localeParamName);
        }
        Cookie[] cookies = request.getCookies();
        //获取cookie
        if(StringUtils.isBlank(localeStr) 
                && StringUtils.isNotBlank(this.localeCookieName)
                && cookies!=null
                && cookies.length >0){
            
            for(Cookie cookie:cookies){
                if(this.localeCookieName.equals(cookie.getName())){
                    localeStr = cookie.getValue();
                }
            }
        }
        
        return localeStr;
    }

	public String getLogOutServerUrl() {
		return logOutServerUrl;
	}

	public void setLogOutServerUrl(String logOutServerUrl) {
		this.logOutServerUrl = logOutServerUrl;
	}

	public String getLogOutBackUrl() {
		return logOutBackUrl;
	}

	public void setLogOutBackUrl(String logOutBackUrl) {
		this.logOutBackUrl = logOutBackUrl;
	}

    public String getLocaleParamName() {
        return localeParamName;
    }

    public void setLocaleParamName(String localeParamName) {
        this.localeParamName = localeParamName;
    }

    public String getLocaleCookieName() {
        return localeCookieName;
    }

    public void setLocaleCookieName(String localeCookieName) {
        this.localeCookieName = localeCookieName;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
