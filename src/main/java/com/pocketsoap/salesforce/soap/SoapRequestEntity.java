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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.httpclient.methods.RequestEntity;

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
	static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	
	public final void writeRequest(OutputStream out) throws IOException {
		XMLOutputFactory f = XMLOutputFactory.newInstance();
		try {
			XMLStreamWriter w = f.createXMLStreamWriter(new BufferedOutputStream(out, 1024), "UTF-8");
			w.writeStartDocument();
			w.writeStartElement("s", "Envelope", SOAP_NS);
			w.writeNamespace("s", SOAP_NS);
			w.writeNamespace("p", PARTNER_NS);
			w.setPrefix("p", PARTNER_NS);
			w.setPrefix("s", SOAP_NS);
			if (hasHeaders()) {
				w.writeStartElement(SOAP_NS, "Header");
				writeHeaders(w);
				w.writeEndElement();
			}
			w.writeStartElement(SOAP_NS, "Body");
			writeBody(w);
			w.writeEndElement();//body
			w.writeEndElement();//envelope
			w.writeEndDocument();
			w.close();
		} catch (XMLStreamException e) {
			throw new IOException("Error generating request xml", e);
		}
	}

	protected void writeElementString(XMLStreamWriter w, String elemNamespace, String elemName, String elemValue) throws XMLStreamException {
		w.writeStartElement(elemNamespace, elemName);
		w.writeCharacters(elemValue);
		w.writeEndElement();
	}
	
	protected boolean hasHeaders() {
		return false;
	}
	
	protected void writeHeaders(XMLStreamWriter w) throws XMLStreamException {
	}
	
	protected abstract void writeBody(XMLStreamWriter w) throws XMLStreamException;
	
	public long getContentLength() {
		return -1;
	}

	public String getContentType() {
		return "text/xml; charset=utf-8";
	}
}
