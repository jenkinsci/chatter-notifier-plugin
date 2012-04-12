package com.pocketsoap.salesforce.rest.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.pocketsoap.salesforce.soap.SessionInfo;

/**
 * Utility class for making a SOQL query via REST
 * @author nlipke-sfdc
 *
 */
public class QueryService {

	/** Ensure no construction */
	private QueryService() {
	}
	
	/**
	 * Do a SOQL query via REST
	 * @param <T>
	 * @param session The session including the URL and the sid
	 * @param query The query to run
	 * @return The query response
	 * @throws IOException If something goes awry
	 */
	public static <T> QueryResponse<T> doQuery(SessionInfo session, String query, Class<T> recordClass) throws IOException {
		URL instanceUrl = new URL(session.instanceServerUrl);
		URL url = new URL(instanceUrl.getProtocol(),
				instanceUrl.getHost(), instanceUrl.getPort(), 
				"/services/data/v24.0/query?q=" + URLEncoder.encode(query, "UTF-8"));
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		try {
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Authorization", "OAuth " + session.sessionId); 

			final InputStream inputStream = conn.getInputStream();
			final ObjectMapper mapper = new ObjectMapper();

			final JsonNode rootNode = mapper.readTree(inputStream);
			inputStream.close();
            
			final int totalSize = mapper.readValue(rootNode.get("totalSize"), Integer.class);
            final boolean done = mapper.readValue(rootNode.get("done"), Boolean.class);

            final QueryResponse<T> response =  new QueryResponse<T>(totalSize, done);
            final List<T> records = response.getRecords();
            for (JsonNode node : rootNode.get("records")) {
                records.add(mapper.readValue(node, recordClass));
            }

			return response;
		} catch (IOException e) {
			e.printStackTrace();
			final InputStream es = conn.getErrorStream();
			if (es != null) {
				int c;

				while ((c = es.read()) != -1) {
					System.err.print((char) c);
				}
				System.err.println();
				es.close();
			}
			throw e;
		}
	}
}
