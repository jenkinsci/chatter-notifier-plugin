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
	protected void writeBody(CompactXMLWriter w) throws IOException {
		w.startElement("create");
		w.addAttribute("xmlns", PARTNER_NS);
		w.startElement("sobject");
		writeElementString(w, "type", "FeedPost");
		writeElementString(w, "parentId", parentId);
		writeElementString(w, "LinkUrl", url);
		writeElementString(w, "title", title);
		writeElementString(w, "body", body);
		w.endElement(); // sobject;
		w.endElement(); // create
	}
}
