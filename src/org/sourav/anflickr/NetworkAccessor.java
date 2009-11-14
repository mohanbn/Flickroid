package org.sourav.anflickr;

import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class NetworkAccessor {
	public static Document invokeAPI(String apiUrl, Map<String, String> parameters) 
								throws ParserConfigurationException, SAXException, IOException {
		StringBuffer reqUrl = new StringBuffer();
		reqUrl.append(apiUrl);
		
		boolean first = true;
		for (String key : parameters.keySet()) {
			reqUrl.append(first ? '?' : '&');
			first = false;
			reqUrl.append(key + "=" + parameters.get(key));
		}
		
		// Request the REST API
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(reqUrl.toString());
		String resp = "";
		resp = httpClient.execute(httpGet, new BasicResponseHandler());
		
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(resp));
		
		DocumentBuilder db;
		db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		return db.parse(is);
	}
	
	public static Object getData(String urlToFetch) throws IOException  {
		URL aUrl = new URL(urlToFetch);
		HttpURLConnection connection = (HttpURLConnection) aUrl.openConnection();
		connection.connect();
		
		return connection.getContent();
	}
}
