package com.pocketsoap.salesforce.rest.chatter;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * A mention @[user] message segment of a Chatter post or comment
 * @author nlipke-sfdc
 *
 */
public class MentionMessageSegment implements MessageSegment {

	/** The type of a Mention segment */
	private static final String TYPE_MENTION = "Mention";
	
	/** The mentioned user's id */
	private String userId;
	
	/**
	 * Create a Mention Segment with no user set
	 */
	public MentionMessageSegment() {
	}
	
	/**
	 * Create a Mention segment with the user id set
	 * @param userId The mentioned user's id
	 */
	public MentionMessageSegment(String userId) {
		this.userId = userId;
	}

	/**
	 * Get the mentioned user's ID
	 * @return user's ID
	 */
	@JsonProperty(value="id")
	public String getUserId() {
		return userId;
	}

	/**
	 * Set the mentioned user's ID
	 * @param userId user's ID
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}



	/**
	 * The segments type
	 * @return Will always return <code>"Mention"</code>
	 */
	public String getType() {
		return TYPE_MENTION;
	}

	@Override
	public String toString() {
		return "MentionMessageSegment [userId=" + userId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MentionMessageSegment other = (MentionMessageSegment) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

}
