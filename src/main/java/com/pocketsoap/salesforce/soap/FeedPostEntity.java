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
