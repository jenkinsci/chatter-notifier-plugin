package com.pocketsoap.salesforce.soap;

/**
 * Info about an already established session.
 * 
 * @author superfell
 */
public class SessionInfo {
	
	SessionInfo(String sessionId, String instanceServerUrl, String userId) {
		this.sessionId = sessionId;
		this.instanceServerUrl = instanceServerUrl;
		this.userId = userId;
	}

	public final String sessionId, instanceServerUrl, userId;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instanceServerUrl == null) ? 0 : instanceServerUrl.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SessionInfo other = (SessionInfo) obj;
		if (instanceServerUrl == null) {
			if (other.instanceServerUrl != null)
				return false;
		} else if (!instanceServerUrl.equals(other.instanceServerUrl))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
}