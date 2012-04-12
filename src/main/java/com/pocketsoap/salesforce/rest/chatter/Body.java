package com.pocketsoap.salesforce.rest.chatter;

import java.util.ArrayList;
import java.util.List;



/**
 * The body of a Chatter comment or post
 * @author nlipke-sfdc
 *
 */
public class Body {
	private final List<MessageSegment> messageSegments = new ArrayList<MessageSegment>();

	/**
	 * Get the list of message segments
	 * @return The ordered list of message segments
	 */
	public List<MessageSegment> getMessageSegments() {
		return messageSegments;
	}

	@Override
	public String toString() {
		return "Body [messageSegments=" + messageSegments + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((messageSegments == null) ? 0 : messageSegments.hashCode());
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
		Body other = (Body) obj;
		if (messageSegments == null) {
			if (other.messageSegments != null)
				return false;
		} else if (!messageSegments.equals(other.messageSegments))
			return false;
		return true;
	}
}
