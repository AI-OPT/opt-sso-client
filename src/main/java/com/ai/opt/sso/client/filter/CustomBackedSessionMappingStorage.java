package com.ai.opt.sso.client.filter;

import javax.servlet.http.HttpSession;

import org.jasig.cas.client.session.SessionMappingStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.opt.sdk.components.mcs.MCSClientFactory;
import com.ai.opt.uni.session.impl.CacheHttpSession;
import com.ai.opt.uni.session.impl.SessionClient;
import com.ai.opt.uni.session.impl.SessionListenerAdaptor;
import com.ai.opt.uni.session.impl.SessionManager;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;

/**
 * CustomBackedSessionMappingStorage
 * Date: 2017年2月9日 <br>
 * Copyright (c) 2017 asiainfo.com <br>
 * 
 * @author
 */
public final class CustomBackedSessionMappingStorage implements SessionMappingStorage {
	private final Logger logger;
	private final static ICacheClient jedis = MCSClientFactory.getCacheClient(SessionClient.getSessionPassNameSpace());
	private String SESSION_KEY_MAPPINGID = "session_key_mappingid";
	private String MAPPINGID_KEY_SESSION = "mappingid_key_session";
	private SessionClient sessionClient = new SessionClient();

	public CustomBackedSessionMappingStorage() {

		this.logger = LoggerFactory.getLogger(super.getClass());
	}

	/**
	 *通过id添加session
	 */
	public synchronized void addSessionById(String mappingId, HttpSession session) {
		this.logger.info("addSessionById:"+ mappingId);
		jedis.hset(SESSION_KEY_MAPPINGID, session.getId(), mappingId);
		jedis.hset(MAPPINGID_KEY_SESSION, mappingId, session.getId());
	}
	/**
	 *通过id移除session
	 */
	public synchronized void removeBySessionById(String sessionId) {
		this.logger.debug("Attempting to remove Session=[{}]", sessionId);
		String key = jedis.hget(SESSION_KEY_MAPPINGID, sessionId);
		jedis.hdel(SESSION_KEY_MAPPINGID, sessionId);
		if (key != null)
			jedis.hdel(MAPPINGID_KEY_SESSION, key);

	}

	/**
	 * 通过匹配的id移除session
	 */
	public synchronized HttpSession removeSessionByMappingId(String mappingId) {
		Object obj = jedis.hget(MAPPINGID_KEY_SESSION, mappingId);
		this.logger.info("removeSessionByMappingId:mappingId"+mappingId);
		if (null == obj)
			return null;
		String sessionId = (String) obj;
		this.logger.info("removeSessionByMappingId:sessionId"+sessionId);
		obj = sessionClient.getSession(SessionManager.SESSION_ID_PREFIX+sessionId);
		if (obj != null) {
			this.logger.info("removeSessionByMappingId:sessionId is not null!");
			CacheHttpSession session = (CacheHttpSession) obj;
			removeBySessionById(sessionId);
			session.setListener(new SessionListenerAdaptor() {
	            public void onInvalidated(CacheHttpSession session) {
	            	sessionClient.delItem(SessionManager.SESSION_ID_PREFIX+session.getId());
	            }
	        });
			return session;
		}
		return null;
	}
}