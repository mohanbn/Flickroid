package org.sourav.anflickr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class User {
	private static final String XML_TAG_REAL_NAME = "realname";
	private static final String XML_TAG_USER_NAME = "username";
	
	private static final String XML_ATTR_ID = "nsid";
	
	private Document user;
	private String id;
	
	public User(String id) {
		this.id = id;
	}
	
	public User(Node item) {
		NamedNodeMap attr = item.getAttributes();
		id = attr.getNamedItem(XML_ATTR_ID).getNodeValue();
	}

	private void fetchUserInfo() throws ParserConfigurationException, SAXException, IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put(FlickrApi.PARAM_API_KEY, Flickr.APIKEY);
		param.put(FlickrApi.PARAM_METHOD, FlickrApi.METHOD_PEOPLE_GETINFO);
		param.put(FlickrApi.PARAM_USER_ID, id);
		
		user = NetworkAccessor.invokeAPI(FlickrApi.API_URL, param);
	}

	public String getRealName() throws ParserConfigurationException, SAXException, IOException {
		if (user == null) {
			fetchUserInfo();
		}
		
		String name = "";
		try {
			name = user.getElementsByTagName(XML_TAG_REAL_NAME).item(0).getChildNodes().item(0).getNodeValue();
		} catch (Exception e) {
			name = user.getElementsByTagName(XML_TAG_USER_NAME).item(0).getChildNodes().item(0).getNodeValue();
		}
		
		return name;
	}
}
