package com.sourav.flickroid;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.sourav.anflickr.Photo;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoActivity extends Activity {
	private ImageView imgPhoto;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Photo p = getIntent().getParcelableExtra("parcelled-photo");
		
		setContentView(R.layout.photo);
		
		imgPhoto = (ImageView) findViewById(R.id.photoImg);
		title = (TextView) findViewById(R.id.photoTitle);
		
		try {
			imgPhoto.setImageBitmap(p.getBitmapPhoto());
			title.setText(p.getPhotoSummary());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
