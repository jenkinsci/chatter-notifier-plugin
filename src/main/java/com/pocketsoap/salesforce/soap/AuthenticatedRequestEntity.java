package com.pocketsoap.salesforce.soap;

import java.io.IOException;

import org.codehaus.plexus.util.xml.CompactXMLWriter;


/**
 * A soap request that includes a SessionHeader in the soap headers.
 * 
 * @author superfell
 *
 */
public abstract class AuthenticatedRequestEntity extends SoapRequestEntity {

	public AuthenticatedRequestEntity(SessionInfo s) {
		this.sid = s.sessionId;
	}
	
	public AuthenticatedRequestEntity(String sessionId) {
		this.sid = sessionId;
	}
	
	private final String sid;

	@Override
	protected boolean hasHeaders() {
		return true;
	}

	@Override
	protected void writeHeaders(CompactXMLWriter w) throws IOException {
		w.startElement("SessionHeader");
		w.addAttribute("xmlns", PARTNER_NS);
		writeElementString(w, "sessionId", sid);
		w.endElement();
	}
}
