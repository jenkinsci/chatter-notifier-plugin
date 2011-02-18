// Copyright (c) 2011 Simon Fell
//
// Permission is hereby granted, free of charge, to any person obtaining a 
// copy of this software and associated documentation files (the "Software"), 
// to deal in the Software without restriction, including without limitation
// the rights to use, copy, modify, merge, publish, distribute, sublicense, 
// and/or sell copies of the Software, and to permit persons to whom the 
// Software is furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included 
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
// THE SOFTWARE.
//

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
