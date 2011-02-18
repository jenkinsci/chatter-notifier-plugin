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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.codehaus.plexus.util.xml.CompactXMLWriter;

/**
 * Base class for serializing a soap message into an HTTP Client request.
 * 
 * @author suprefell
 */
public abstract class SoapRequestEntity implements RequestEntity {

	public boolean isRepeatable() {
		return true;
	}
	
	static final String PARTNER_NS = "urn:partner.soap.sforce.com";
			
	public final void writeRequest(OutputStream out) throws IOException {
		Writer ws = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"), 1024);
		CompactXMLWriter w = new CompactXMLWriter(ws);
		w.startElement("Envelope");
		w.addAttribute("xmlns", "http://schemas.xmlsoap.org/soap/envelope/");
		if (hasHeaders()) {
			w.startElement("Header");
			writeHeaders(w);
			w.endElement();
		}
		w.startElement("Body");
		writeBody(w);
		w.endElement();//body
		w.endElement();//envelope
		ws.close();
	}

	protected void writeElementString(CompactXMLWriter w, String elementName, String elementValue) {
		w.startElement(elementName);
		w.writeText(elementValue);
		w.endElement();
	}
	
	protected boolean hasHeaders() {
		return false;
	}
	
	protected void writeHeaders(CompactXMLWriter w) throws IOException {
	}
	
	protected abstract void writeBody(CompactXMLWriter w) throws IOException;
	
	public long getContentLength() {
		return -1;
	}

	public String getContentType() {
		return "text/xml; charset=utf-8";
	}
}
