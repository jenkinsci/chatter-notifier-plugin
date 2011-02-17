package com.pocketsoap.salesforce.soap;

/**
 * @author superfell
 */
public class SaveResult {

	SaveResult(String id, String statusCode, String errorMessage) {
		this.id = id;
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
	}
	
	public final String id, statusCode, errorMessage;
	
	public boolean isSuccess() {
		return id != null && id.length() >= 15;
	}
}