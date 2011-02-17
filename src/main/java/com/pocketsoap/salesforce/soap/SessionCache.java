package com.pocketsoap.salesforce.soap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * We keep a cache of sessions that we've created so that we don't end up calling login every time we need to post a build result.
 * 
 * @author superfell
 */
public class SessionCache {

	private static final SessionCache INSTANCE = new SessionCache();
	
	public static SessionCache get() {
		return INSTANCE;
	}
	
	private final ConcurrentHashMap<CredentialsInfo, SessionInfo> sessions = new ConcurrentHashMap<CredentialsInfo, SessionInfo>();
	
	public SessionInfo getCachedSessionInfo(String username, String password, String serverUrl) {
		return getCachedSessionInfo(new CredentialsInfo(username, password, serverUrl));
	}
	
	public SessionInfo getCachedSessionInfo(CredentialsInfo c) {
		return sessions.get(c);
	}

	public void revoke(CredentialsInfo k) {
		sessions.remove(k);
	}

	// this is not cheap if the session cache is large.
	public void revoke(SessionInfo s) {
		for (Map.Entry<CredentialsInfo, SessionInfo> e : sessions.entrySet()) {
			if (e.getValue().equals(s))
				sessions.remove(e.getKey());
		}
	}
	
	public void add(CredentialsInfo c, SessionInfo s) {
		sessions.put(c,s);
	}
}
