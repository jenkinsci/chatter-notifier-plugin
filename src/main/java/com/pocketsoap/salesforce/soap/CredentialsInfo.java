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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Container class for credential sets.
 * 
 * @author superfell
 */
public class CredentialsInfo {

	public CredentialsInfo(String username, String password, String loginServerUrl) throws MalformedURLException {
		this.username = username;
		this.password = password;
		this.serverUrl = normalizeServerUrl(loginServerUrl);
	}
	
	private final String username, password;
	private final URL serverUrl;

	private static final String DEFAULT_SERVER_URL = "https://login.salesforce.com/";
	
	// url can be null, or can be an invalid URL, or could contain a path, if there's a path we need to strip it off
	// if url is null, we need to default it.
	private URL normalizeServerUrl(String url) throws MalformedURLException {
		if (url != null) url = url.trim();
		if (url == null || url.length() == 0) url = DEFAULT_SERVER_URL;
		URL u = new URL(url);
		return new URL(u, "/");
	}
	
	public String getUsername() {
		return username;
	}
	
	public URL getLoginServerUrl() {
		return serverUrl;
	}
	
	String getPassword() {
		return password;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((serverUrl == null) ? 0 : serverUrl.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof CredentialsInfo)) return false;
		CredentialsInfo other = (CredentialsInfo) obj;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (serverUrl == null) {
			if (other.serverUrl != null)
				return false;
		} else if (!serverUrl.equals(other.serverUrl))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}