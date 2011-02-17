package com.pocketsoap.salesforce.soap;

import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

/**
 * 
 * @author superfell
 */
public class ChatterClient {

	public ChatterClient(String username, String password, String loginServerUrl) {
		this.credentials = new CredentialsInfo(username, password, loginServerUrl);
	}

	private final CredentialsInfo credentials;
	private SessionInfo session;
	
	public void postBuild(String recordId, String title, String resultsUrl, String testHealth) throws IOException, XMLStreamException, FactoryConfigurationError {
		establishSession();
		String body = testHealth == null ? title : title + "\n" + testHealth;
		String pid = recordId == null || recordId.length() == 0 ? session.userId : recordId;
		createFeedPost(pid, title, resultsUrl, body);
		
	}
	
	void establishSession() throws IOException, XMLStreamException, FactoryConfigurationError {
		SessionInfo s = SessionCache.get().getCachedSessionInfo(credentials);
		if (s == null) {
			s = performLogin();
			SessionCache.get().add(credentials, s);
		}
		session = s;
	}
	
	private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String SF_NS = "urn:partner.soap.sforce.com";
	
	SessionInfo performLogin() throws IOException, XMLStreamException, FactoryConfigurationError {
		return makeSoapRequest(this.credentials.getLoginServerUrl() + "/services/Soap/u/21.0", new LoginRequestEntity(credentials), new LoginResponseParser());
	}
	
	SaveResult createFeedPost(String recordId, String title, String url, String testHealth) throws XMLStreamException, IOException {
		return makeSoapRequest(session.instanceServerUrl, new FeedPostEntity(session.sessionId, recordId, title, url, testHealth), new SaveResultParser());
	}
	
	private static abstract class ResponseParser<T> {
		// parse a response, the reader has already been moved through the soap gubins, 
		// and is at the first child of body.
		abstract T parse(XMLStreamReader r) throws XMLStreamException;
	}
	
	private static class SaveResultParser extends ResponseParser<SaveResult> {

		@Override
		SaveResult parse(XMLStreamReader rdr) throws XMLStreamException {
			String id = null, sc = null, msg = null;
			rdr.require(XMLStreamReader.START_ELEMENT, SF_NS, "createResponse");
			rdr.nextTag();
			rdr.require(XMLStreamReader.START_ELEMENT, SF_NS, "result");
			while (rdr.next() != XMLStreamReader.END_DOCUMENT) {
				if (rdr.getEventType() == XMLStreamReader.START_ELEMENT) {
					String ln = rdr.getLocalName();
					if (ln.equals("id")) 
						id = rdr.getElementText();
					else if (ln.equals("statusCode"))
						sc = rdr.getElementText();
					else if (ln.equals("message")) 
						msg = rdr.getElementText();
				}
			}
			return new SaveResult(id, sc, msg);
		}
	}
	
	private static class LoginResponseParser extends ResponseParser<SessionInfo> {

		@Override
		SessionInfo parse(XMLStreamReader rdr) throws XMLStreamException {
			String sid = null, instanceUrl = null, userId = null;
			rdr.require(XMLStreamReader.START_ELEMENT, SF_NS, "loginResponse");
			while (rdr.next() != XMLStreamReader.END_DOCUMENT) {
				if (rdr.getEventType() == XMLStreamReader.START_ELEMENT) {
					String ln = rdr.getLocalName();
					if (ln.equals("sessionId")) 
						sid = rdr.getElementText();
					else if (ln.equals("serverUrl")) 
						instanceUrl = rdr.getElementText();
					else if (ln.equals("userId"))
						userId = rdr.getElementText();
				}
			}
			return new SessionInfo(sid, instanceUrl, userId);
		}
	}
	
	private <T> T makeSoapRequest(String serverUrl, RequestEntity req, ResponseParser<T> respParser) throws XMLStreamException, IOException {
		PostMethod post = new PostMethod(serverUrl);
		post.addRequestHeader("SOAPAction", "\"\"");
		post.setRequestEntity(req);

		HttpClient http = new HttpClient();
		int sc = http.executeMethod(post);
		System.out.println("request to " + post.getURI() + " returned " + sc);
		
		XMLInputFactory f = XMLInputFactory.newInstance();
		f.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);

		XMLStreamReader rdr = f.createXMLStreamReader(post.getResponseBodyAsStream());
		rdr.require(XMLStreamReader.START_DOCUMENT, null, null);
		rdr.nextTag();
		rdr.require(XMLStreamReader.START_ELEMENT, SOAP_NS, "Envelope");
		rdr.nextTag();
		// TODO, should handle a Header appearing in the response.
		rdr.require(XMLStreamReader.START_ELEMENT, SOAP_NS, "Body");
		rdr.nextTag();
		if (rdr.getLocalName().equals("Fault")) {
			throw handleSoapFault(rdr);
		}
		try {
			T response = respParser.parse(rdr);
			while (rdr.hasNext()) rdr.next();
			return response;
		} finally {
			try {
				rdr.close();
			} finally {
				post.releaseConnection();
			}
		}
	}
	
	private RuntimeException handleSoapFault(XMLStreamReader r) {
		return new RuntimeException("boomn");// TODO
	}
}
