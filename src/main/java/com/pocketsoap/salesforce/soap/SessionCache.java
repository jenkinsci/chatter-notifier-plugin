// Copyright (c) 2011 Simon Fell
//
// Permission is hereby granted, free of charge, to any person obtaining a 
// copy of this software and associated documentation files (the "Software"), 
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense, 
// and/or sell copies of the Software, and to permit persons to whom the 
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included 
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
// THE SOFTWARE.
//

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
