package org.sourav.anflickr;

public class FlickrApi {
	public static final String API_URL = "http://api.flickr.com/services/rest";
	
	public static final String METHOD_INTERESTINGNESS_GETLIST = "flickr.interestingness.getList";
	public static final String METHOD_PEOPLE_GETINFO = "flickr.people.getInfo";
	public static final String METHOD_PHOTO_GETINFO = "flickr.photos.getInfo";
	
	public static final String PARAM_API_KEY = "api_key";
	public static final String PARAM_METHOD = "method";
	public static final String PARAM_SECRET = "secret";
	
	public static final String PARAM_DATE = "date";
	public static final String PARAM_PER_PAGE = "per_page";
	public static final String PARAM_PAGE = "page";
	
	public static final String PARAM_USER_ID = "user_id";
	public static final String PARAM_PHOTO_ID = "photo_id";
}
