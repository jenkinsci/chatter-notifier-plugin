package com.pocketsoap.salesforce.rest.user;


import org.codehaus.jackson.annotate.JsonProperty;


/**
 * Represents an SFDC user
 * @author nlipke-sfdc
 *
 */
public class User {
	private Atrributes attributes;
	private String id;
	
	/**
	 * Get attributes about the User record
	 * @return
	 */
	public Atrributes getAttributes() {
		return attributes;
	}
	
	
	/**
	 * Get the user's ID
	 * @return their ID
	 */
	@JsonProperty(value="Id")
	public String getId() {
		return id;
	}


	@Override
	public String toString() {
		return "User [attributes=" + attributes + ", id=" + id + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		User other = (User) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}