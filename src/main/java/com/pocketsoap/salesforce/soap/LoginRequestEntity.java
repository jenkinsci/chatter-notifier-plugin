/**
 * 
 */
package com.pocketsoap.salesforce.soap;

import java.io.IOException;

import org.codehaus.plexus.util.xml.CompactXMLWriter;

/**
 * A Login request.
 * 
 * @author superfell
 */
public class LoginRequestEntity extends SoapRequestEntity {

	public LoginRequestEntity(CredentialsInfo c) {
		this.credentials = c;
	}

	private final CredentialsInfo credentials;
	
	@Override
	protected void writeBody(CompactXMLWriter w) throws IOException {
		
		w.startElement("login");
		w.addAttribute("xmlns", PARTNER_NS);
		
		writeElementString(w, "username", credentials.getUsername());
		writeElementString(w, "password", credentials.getPassword());

		w.endElement();//login
	}
}
