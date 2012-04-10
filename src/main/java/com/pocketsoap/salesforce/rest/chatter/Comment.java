package com.pocketsoap.salesforce.rest.chatter;



/**
 * Represents a Chatter comment
 * @author nlipke-sfdc
 *
 */
public class Comment {
	
	/**
	 * The body of the comment
	 */
	private final Body body = new Body();

	/**
	 * Get the body
	 * @return the body
	 */
	public Body getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "Comment [body=" + body + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
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
		Comment other = (Comment) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		return true;
	}
}
