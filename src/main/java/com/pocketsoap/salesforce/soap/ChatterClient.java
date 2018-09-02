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

import com.pocketsoap.salesforce.rest.chatter.Comment;
import com.pocketsoap.salesforce.rest.chatter.MentionMessageSegment;
import com.pocketsoap.salesforce.rest.chatter.MessageSegment;
import com.pocketsoap.salesforce.rest.chatter.TextMessageSegment;
import com.pocketsoap.salesforce.rest.user.UserService;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class handles all the API calls to Salesforce/Chatter using the SOAP API.
 * This just implements the miniumum set of calls we need, not the entire API.
 * 
 * @author superfell
 */
public class ChatterClient {
	public ChatterClient(String username, String password, String loginServerUrl) throws MalformedURLException {
		this.credentials = new CredentialsInfo(username, password, loginServerUrl);
	}

	private final CredentialsInfo credentials;
	private SessionInfo session;

	public String postBuild(String recordId, String title, String resultsUrl, String testHealth, Map<String, String> suspects) throws IOException, XMLStreamException, FactoryConfigurationError {
		return postBuild(recordId, title, resultsUrl, testHealth, suspects, true);
	}
	
	public void delete(String id) throws XMLStreamException, IOException {
		establishSession();
		// SaveResult is the same as DeleteResult.
		SaveResult r = makeSoapRequest(session.instanceServerUrl,
				new DeleteRequestEntity(session.sessionId, id),
				new SaveResultParser());
		if (!r.success)
			throw new SaveResultException(r);
	}

	public String postText(String recordId, String title, String body, String resultsUrl) throws IOException, XMLStreamException, FactoryConfigurationError {
		establishSession();

		String pid = recordId == null || recordId.length() == 0 ? session.userId : recordId;
		try {
			return createSoapFeedPost(pid, title, resultsUrl, body);
		} catch (SoapFaultException ex) {
			// if we were using a cached session, then it could have expired
			// by now, so we check for INVALID_SESSION, and if we see that
			// error, we'll flush the session cache and try again.
			if (ex.getFaultCode().toLowerCase().contains("session")) {
				SessionCache.get().revoke(credentials);
				return postText(recordId, title, resultsUrl, body);
			}
			throw ex;
		}
	}

	private String postBuild(String recordId, String title, String resultsUrl, String testHealth, Map<String, String> suspects, boolean retryOnInvalidSession) throws IOException, XMLStreamException, FactoryConfigurationError {
		establishSession();
		String body = testHealth == null ? title : title + "\n" + testHealth;
		if (body.length() > 1000) body = body.substring(0, 998) + "\u2026";	// ...


		String pid = recordId == null || recordId.length() == 0 ? session.userId : recordId;
		try {
			String postId = createSoapFeedPost(pid, title, resultsUrl, body);
			if (suspects != null && !suspects.isEmpty()) {
				postSuspectsComment(postId, suspects, true);
			}
			return postId;
		} catch (SoapFaultException ex) {
			// if we were using a cached session, then it could have expired
			// by now, so we check for INVALID_SESSION, and if we see that
			// error, we'll flush the session cache and try again.
			if (retryOnInvalidSession && ex.getFaultCode().toLowerCase().contains("session")) {
				SessionCache.get().revoke(credentials);
				return postBuild(recordId, title, resultsUrl, testHealth, suspects, false);
			}
			throw ex;
		}
	}


	private String postSuspectsComment(String postId,  Map<String, String> suspects, boolean retryOnInvalidSession)
			throws IOException, XMLStreamException,
			FactoryConfigurationError {
		URL instanceUrl = new URL(session.instanceServerUrl);
		URL url = new URL(instanceUrl.getProtocol(),
				instanceUrl.getHost(), instanceUrl.getPort(), 
				"/services/data/v24.0/chatter/feed-items/" + postId + "/comments");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		
		try {
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "OAuth " + session.sessionId); 


			final Comment comment = new Comment();
			final List<MessageSegment> messageSegments = comment.getBody().getMessageSegments();
			messageSegments.add(new TextMessageSegment("Suspects: "));
			
			Iterator<Entry<String, String>> it = suspects.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> ids = it.next();

				String sfdcId = ids.getValue();
				if (sfdcId != null) {
					//Convert the login to an SFDC ID
					sfdcId = UserService.getUserId(session, sfdcId);
				}
				
				if (sfdcId == null) { //not mapped
					messageSegments.add(new TextMessageSegment(ids.getKey()));
				} else {
					messageSegments.add(new MentionMessageSegment(sfdcId));
				}

				if (it.hasNext()) {
					messageSegments.add(new TextMessageSegment(", "));
				}
			}

			final OutputStream bodyStream = conn.getOutputStream();
			ObjectMapper mapper = new ObjectMapper();
			MappingJsonFactory jsonFactory = new MappingJsonFactory();
			JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(bodyStream);
			mapper.writeValue(jsonGenerator, comment);
			bodyStream.close();
			conn.getInputStream().close(); //Don't care about the response
		} catch (IOException e) {
			if (retryOnInvalidSession && conn.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
				SessionCache.get().revoke(credentials);
				return postSuspectsComment(postId, suspects, false);
			}

			StringBuffer error = new StringBuffer();
			try (BufferedReader r = new BufferedReader(new InputStreamReader(conn.getErrorStream(), conn.getContentEncoding()))) {
				String line;
				while ((line = r.readLine()) != null) {
					error.append(line);
					error.append('\n');
				}
			}
			System.out.println(error);
			throw e;
		}
		return null;
	}
	
	void establishSession() throws IOException, XMLStreamException, FactoryConfigurationError {
		SessionInfo s = SessionCache.get().getCachedSessionInfo(credentials);
		if (s == null)  s = performLogin();
		session = s;
	}
	
	private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
	private static final String SF_NS = "urn:partner.soap.sforce.com";
	
	public SessionInfo performLogin() throws IOException, XMLStreamException, FactoryConfigurationError {
		SessionInfo si = makeSoapRequest(new URL(this.credentials.getLoginServerUrl(), "/services/Soap/u/21.0"), new LoginRequestEntity(credentials), new LoginResponseParser());
		SessionCache.get().add(credentials, si);
		return si;
	}
	
	String createSoapFeedPost(String recordId, String title, String url, String testHealth) throws XMLStreamException, IOException {
		SaveResult sr = makeSoapRequest(session.instanceServerUrl, new FeedPostEntity(session.sessionId, recordId, title, url, testHealth), new SaveResultParser());
		if (sr.success) return sr.id;
		throw new SaveResultException(sr);
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
			boolean success = false;
			rdr.require(XMLStreamReader.START_ELEMENT, SF_NS, null);
			if (!rdr.getLocalName().endsWith("Response"))
				throw new XMLStreamException("expected element named *Response but was " + rdr.getLocalName(), rdr.getLocation());
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
					else if (ln.equals("success")) {
						String v = rdr.getElementText();
						success = v.equals("1") || v.equalsIgnoreCase("true");
					}
				}
			}
			return new SaveResult(success, id, sc, msg);
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

	private <T> T makeSoapRequest(URL serverUrl, RequestEntity req, ResponseParser<T> respParser) throws XMLStreamException, IOException {
		return makeSoapRequest(serverUrl.toString(), req, respParser);
	}
	
	private <T> T makeSoapRequest(String serverUrl, RequestEntity req, ResponseParser<T> respParser) throws XMLStreamException, IOException {
		PostMethod post = new PostMethod(serverUrl);
		post.addRequestHeader("SOAPAction", "\"\"");
		post.setRequestEntity(req);

		HttpClient http = getHttpClient(Jenkins.getInstance());
		int sc = http.executeMethod(post);
		if (sc != 200 && sc != 500)
			throw new IOException("request to " + serverUrl + " returned unexpected HTTP status code of " + sc + ", check configuration.");
		
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

	/**
	 * Set up HttpClient based on Jenkins proxy values. Set up proxy host, port, username, and password.
	 * TODO: Handle noProxyHosts
	 *
	 * @param jenkins typically Jenkins.getInstance(). Null check is included
	 * @return configured HttpClient with proxy configuration.
	 */
	static HttpClient getHttpClient(Jenkins jenkins) {
		HttpClient http = new HttpClient();
		if (jenkins != null) {
			ProxyConfiguration proxyConfiguration = jenkins.proxy;
			if (proxyConfiguration != null && StringUtils.isNotEmpty(proxyConfiguration.name)) {
				ProxyHost proxyHost = new ProxyHost(proxyConfiguration.name, proxyConfiguration.port);
				http.getHostConfiguration().setProxyHost(proxyHost);
				if (StringUtils.isNotEmpty(proxyConfiguration.getUserName())) {
					AuthScope authScope = new AuthScope(proxyConfiguration.name, proxyConfiguration.port);
					Credentials credentials =
							new UsernamePasswordCredentials(
									proxyConfiguration.getUserName(),
									proxyConfiguration.getPassword());
					http.getState().setProxyCredentials(authScope, credentials);
				}
			}
		}
		return http;
	}

	// read a soap fault message and turn it into an exception
	private RuntimeException handleSoapFault(XMLStreamReader rdr) throws XMLStreamException {
		String fc = null, fs = null;
		while (rdr.next() != XMLStreamReader.END_DOCUMENT) {
			if (rdr.getEventType() == XMLStreamReader.START_ELEMENT) {
				String ln = rdr.getLocalName();
				if (ln.equals("faultcode")) 
					fc = rdr.getElementText();
				else if (ln.equals("faultstring"))
					fs = rdr.getElementText();
			}
		}
		return new SoapFaultException(fc, fs);
	}
}
