package com.pocketsoap.salesforce.soap;

/**
 * Container class for credential sets.
 * 
 * @author superfell
 */
public class CredentialsInfo {

	public CredentialsInfo(String username, String password, String loginServerUrl) {
		this.username = username;
		this.password = password;
		this.serverUrl = loginServerUrl == null || loginServerUrl.length() == 0 ? "https://login.salesforce.com" : loginServerUrl;
	}

	private final String username, password, serverUrl;

	public String getUsername() {
		return username;
	}
	
	public String getLoginServerUrl() {
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