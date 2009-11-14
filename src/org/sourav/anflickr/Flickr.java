package org.sourav.anflickr;


public class Flickr {
	public static String APIKEY;
	public static String SECRET;
	
	private Interestingness interesting;
	
	public Flickr(String apiKey) {
		APIKEY = apiKey;
	}
	
	public Interestingness getInterestingnessInterface() {
		if (interesting == null) {
			interesting = new Interestingness();
		}
		
		return interesting;
	}
}
