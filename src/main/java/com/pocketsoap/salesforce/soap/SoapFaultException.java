package com.pocketsoap.salesforce.soap;

/**
 * An Exception that represents a received Soap Fault message
 * @author superfell
 */
public class SoapFaultException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	SoapFaultException(String code, String string) {
		super(code + " : " + string);
		this.faultCode = code;
		this.faultString = string;
	}
	
	private final String faultCode, faultString;
	
	public String getFaultCode() {
		return faultCode;
	}
	
	public String getFaultString() {
		return faultString;
	}
}