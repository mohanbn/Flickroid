package org.sourav.anflickr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Interestingness {
	private static final String XML_TAG_PHOTO = "photo";
	
	public Vector<Photo> getPhotos(String date, int perPage, int page) 
								throws ParserConfigurationException, SAXException, IOException {
		
		Vector<Photo> p = new Vector<Photo>();
		Map <String, String> param = new HashMap<String, String>();
		
		param.put(FlickrApi.PARAM_API_KEY, Flickr.APIKEY);
		param.put(FlickrApi.PARAM_METHOD, FlickrApi.METHOD_INTERESTINGNESS_GETLIST);
		
		if (date != null) {
			param.put(FlickrApi.PARAM_DATE, date);
		}
		
		if (perPage > 0) {
			param.put(FlickrApi.PARAM_PER_PAGE, String.valueOf(perPage));
		}
		
		if (page > 0) {
			param.put(FlickrApi.PARAM_PAGE, String.valueOf(page));
		}
		
		Document d = NetworkAccessor.invokeAPI(FlickrApi.API_URL, param);
		
		NodeList list = d.getElementsByTagName(XML_TAG_PHOTO);
		for (int i=0; i<list.getLength(); i++) {
			p.add(new Photo(list.item(i)));
		}
		
		return p;
	}
	
	public Vector<Photo> getPhotos() throws ParserConfigurationException, SAXException, IOException {
		return getPhotos(null, 100, 0);
	}
	
}
