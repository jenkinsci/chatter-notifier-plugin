package com.pocketsoap.salesforce.rest.user;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.pocketsoap.salesforce.rest.query.QueryResponse;
import com.pocketsoap.salesforce.rest.query.QueryService;
import com.pocketsoap.salesforce.soap.SessionInfo;

/**
 * Fetch a user if they exist
 * @author nlipke-sfdc
 *
 */
public class UserService {
	private final static Map<String, String> cachedUserIds = new ConcurrentHashMap<String, String>();
	
	/** Don't construct me */
	private UserService() {
		
	}
	
	/**
	 * Get the user's ID
	 * @param login the SFDC login (email format)
	 * @param session the user's session
	 * @return The user's ID or null (if they don't exist)
	 * @throws IOException The user can't be fetched
	 */
	public static String getUserId(SessionInfo session, String login) throws IOException {
		String id = cachedUserIds.get(login);
		
		if (id == null) {
			final QueryResponse<User> response = QueryService.doQuery(session, "select ID from USER where username='" + login + "'", User.class);
			if (response.getTotalSize() == 1) {
				id = response.getRecords().get(0).getId();
				cachedUserIds.put(login, id);
			}
		}
		return id;
	}
}
