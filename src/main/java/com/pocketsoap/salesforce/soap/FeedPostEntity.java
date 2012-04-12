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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A Create FeedPost request.
 * 
 * @author simon
 *
 */
public class FeedPostEntity extends AuthenticatedRequestEntity {

	public FeedPostEntity(String sid, String parentId, String title, String url, String body) {
		super(sid);
		this.parentId = parentId;
		this.title = title;
		this.url = url;
		this.body = body;
	}
	
	private final String parentId, title, url, body;
	
	@Override
	protected void writeBody(XMLStreamWriter w) throws XMLStreamException {
		w.writeStartElement(PARTNER_NS, "create");
		w.writeStartElement(PARTNER_NS, "sobject");
		writeElementString(w, PARTNER_NS, "type", "FeedItem");
		writeElementString(w, PARTNER_NS, "parentId", parentId);
		writeElementString(w, PARTNER_NS, "LinkUrl", url);
		writeElementString(w, PARTNER_NS, "title", title);
		writeElementString(w, PARTNER_NS, "body", body);
		w.writeEndElement(); // sobject;
		w.writeEndElement(); // create
	}
}
