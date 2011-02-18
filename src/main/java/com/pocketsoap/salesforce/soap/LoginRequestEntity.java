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
