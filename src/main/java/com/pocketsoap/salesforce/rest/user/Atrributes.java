package com.pocketsoap.salesforce.rest.user;

/**
 * Represents user attributes
 * @author nlipke-sfdc
 *
 */
public class Atrributes {
	private String type;
	private String url;

	public Atrributes(String type, String url) {
		this.type = type;
		this.url = url;
	}

	/**
	 * Get the record type
	 * @return <code>"User"</code>
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get the URL (not including scheme host and port to the user's full record)
	 * @return The partial URL
	 */
	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return "Atrributes [type=" + type + ", url=" + url + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Atrributes other = (Atrributes) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
