/**
 * 
 */
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
