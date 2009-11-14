package org.sourav.anflickr;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Parcel;
import android.os.Parcelable;

public class Photo extends Binder implements Parcelable {
	private static final int READ_BLOCK = 5120;
	private static final String XML_TAG_PHOTO = "photo";
	private static final String XML_TAG_TITLE = "title";
	private static final String XML_TAG_OWNER = "owner";
	
	private static final String XML_ATTR_FARM = "farm";
	private static final String XML_ATTR_ID = "id";
	private static final String XML_ATTR_SERVER = "server";
	private static final String XML_ATTR_SECRET = "secret";
	
	private String id;
	private String secret;
	private Bitmap bitmapPhoto;
	
	private Document photo;
	private User owner;
	
    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() { 
		public Photo createFromParcel(Parcel in) { 
		    return (Photo)in.readStrongBinder(); 
		}

		public Photo[] newArray(int size) {
			return new Photo[size];
		}
    };
	
	public Photo(String id, String secret) {
		this.id = id;
		this.secret = secret;
	}
	
	public Photo(Node item) {
		NamedNodeMap attr = item.getAttributes();
		id = attr.getNamedItem(XML_ATTR_ID).getNodeValue();
		secret = attr.getNamedItem(XML_ATTR_SECRET).getNodeValue();
	}
	
	private void fetchPhotoInfo() throws ParserConfigurationException, SAXException, IOException {
		Map<String, String> param = new HashMap<String, String>();
		param.put(FlickrApi.PARAM_API_KEY, Flickr.APIKEY);
		param.put(FlickrApi.PARAM_METHOD, FlickrApi.METHOD_PHOTO_GETINFO);
		param.put(FlickrApi.PARAM_PHOTO_ID, id);
		if (secret != null) param.put(FlickrApi.PARAM_SECRET, secret);
		
		photo = NetworkAccessor.invokeAPI(FlickrApi.API_URL, param);
		if (secret == null) secret = photo.getElementsByTagName(XML_TAG_PHOTO).item(0).getAttributes().getNamedItem(XML_ATTR_SECRET).getNodeValue();
		if (owner == null) owner = new User(photo.getElementsByTagName(XML_TAG_OWNER).item(0));
	}
	
	public String getPhotoUrl() throws ParserConfigurationException, SAXException, IOException {
		if (photo == null) {
			fetchPhotoInfo();
		}
		
		NamedNodeMap attr = photo.getElementsByTagName(XML_TAG_PHOTO).item(0).getAttributes();
		String url = "http://farm";
		url += attr.getNamedItem(XML_ATTR_FARM).getNodeValue() + ".static.flickr.com/";
		url += attr.getNamedItem(XML_ATTR_SERVER).getNodeValue() + "/";
		url += id + "_";
		url += secret + ".jpg";
		
		return url;
	}
	
	public String getPhotoTags() {
		return null;
	}
	
	public String[] getPhotoComments(int num) {
		return null;
	}
	
	public String getPhotoSummary() throws ParserConfigurationException, SAXException, IOException {
		if (photo == null) {
			fetchPhotoInfo();
		}
		
		String summary = "";
		try {
			summary += photo.getElementsByTagName(XML_TAG_TITLE).item(0).getChildNodes().item(0).getNodeValue();
		} catch(Exception e) {
		}
		summary += " by " + owner.getRealName();
		
		return summary;
	}
	
	public Bitmap getBitmapPhoto() throws IOException, ParserConfigurationException, SAXException {
		if (photo == null) {
			fetchPhotoInfo();
		}
		
		if (bitmapPhoto == null) {
			InputStream is = (InputStream) NetworkAccessor.getData(this.getPhotoUrl());
			int readBlock = READ_BLOCK;
			byte[] ba = new byte[readBlock];
			ByteArrayBuffer buf = new ByteArrayBuffer(readBlock);
			
			int charRead = is.read(ba, 0, readBlock);
			while(charRead > 0) {
				buf.append(ba, 0, charRead);
				charRead = is.read(ba, 0, readBlock);
			}
			
			bitmapPhoto = BitmapFactory.decodeByteArray(buf.buffer(), 0, buf.length());
		}
		return bitmapPhoto;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStrongBinder(this);
	}

}
