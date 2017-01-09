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

public final class CustomBackedSessionMappingStorage implements SessionMappingStorage {
	private final Logger logger;
	private final static ICacheClient jedis = MCSClientFactory.getCacheClient(SessionClient.getSessionPassNameSpace());
	private String SESSION_KEY_MAPPINGID = "session_key_mappingid";
	private String MAPPINGID_KEY_SESSION = "mappingid_key_session";
	private SessionClient sessionClient = new SessionClient();

	public CustomBackedSessionMappingStorage() {

		this.logger = LoggerFactory.getLogger(super.getClass());
	}

	public synchronized void addSessionById(String mappingId, HttpSession session) {
		this.logger.info("addSessionById:"+ mappingId);
		jedis.hset(SESSION_KEY_MAPPINGID, session.getId(), mappingId);
		jedis.hset(MAPPINGID_KEY_SESSION, mappingId, session.getId());
	}

	public synchronized void removeBySessionById(String sessionId) {
		this.logger.debug("Attempting to remove Session=[{}]", sessionId);
//		Object obj = sessionClient.getSession("R_JSID_"+sessionId);
//		if (obj != null) {
//			HttpSession session = (HttpSession) obj;
//			session.invalidate();
//		}
		String key = jedis.hget(SESSION_KEY_MAPPINGID, sessionId);
		jedis.hdel(SESSION_KEY_MAPPINGID, sessionId);
		if (key != null)
			jedis.hdel(MAPPINGID_KEY_SESSION, key);

	}

	public synchronized HttpSession removeSessionByMappingId(String mappingId) {
		Object obj = jedis.hget(MAPPINGID_KEY_SESSION, mappingId);
		this.logger.info("removeSessionByMappingId:mappingId"+mappingId);
		if (null == obj)
			return null;
		String sessionId = (String) obj;
		this.logger.info("removeSessionByMappingId:sessionId"+sessionId);
		obj = sessionClient.getSession("R_JSID_"+sessionId);
		if (obj != null) {
			CacheHttpSession session = (CacheHttpSession) obj;
			removeBySessionById(sessionId);
			session.setListener(new SessionListenerAdaptor() {
	            public void onInvalidated(CacheHttpSession session) {
	            	sessionClient.delItem("R_JSID_"+session.getId());
	            }
	        });
			return session;
		}
		return null;
	}
}