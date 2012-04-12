package com.pocketsoap.salesforce.rest.query;

import java.util.ArrayList;
import java.util.List;


/**
 * The response from a SOQL query
 * @author nlipke-sfdc
 *
 */
public class QueryResponse<T> {
	private int totalSize;
	private boolean done;
	private List<T> records;

	/** Default constructor  */
	public QueryResponse() {
	}
	
	/**
	 * Create a response with it's size and done set
	 * @param totalSize the number of records
	 * @param done is the query done
	 */
	public QueryResponse(int totalSize, boolean done) {
		super();
		this.totalSize = totalSize;
		this.done = done;
		this.records = new ArrayList<T>(totalSize);
	}
	
	/**
	 * The number of records
	 * @return 0 or more
	 */
	public int getTotalSize() {
		return totalSize;
	}
	
	/**
	 * Did the query finish
	 * @return true if it's done
	 */
	public boolean isDone() {
		return done;
	}
	
	/**
	 * Get the individual records
	 * @return the records if any
	 */
	public List<T> getRecords() {
		return records;
	}

	@Override
	public String toString() {
		return "QueryResponse [totalSize=" + totalSize + ", done=" + done
				+ ", records=" + records + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (done ? 1231 : 1237);
		result = prime * result + ((records == null) ? 0 : records.hashCode());
		result = prime * result + totalSize;
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
		@SuppressWarnings("unchecked")
		QueryResponse<T> other = (QueryResponse<T>) obj;
		if (done != other.done)
			return false;
		if (records == null) {
			if (other.records != null)
				return false;
		} else if (!records.equals(other.records))
			return false;
		if (totalSize != other.totalSize)
			return false;
		return true;
	}
}
