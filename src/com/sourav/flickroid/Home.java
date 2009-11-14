package com.sourav.flickroid;

import java.io.File;
import java.util.Vector;

import org.sourav.anflickr.Flickr;
import org.sourav.anflickr.Photo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.flickroid.ShakeListener.OnShakeListener;

public class Home extends Activity implements OnShakeListener {
	private static final int CAPTURE_CAMERA = 1;
	
	ImageView imgSplash;
	TextView txtSummary;
	ImageButton  btnCamera;
	ProgressDialog loadingDialog;
	int randomCount = 0;
	Home self;

	Flickr flickr;
	ShakeListener sl;
	Vector<Photo> interestingPhotos;
	int currentlyShown;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	self = this;
    	
    	flickr = new Flickr("0dddd8f906d6c69a2924275967ecf441");
    	sl = new ShakeListener(this);
    	
        imgSplash = (ImageView) findViewById(R.id.homeImgSplash);
        txtSummary = (TextView) findViewById(R.id.homeTvSummary);
        btnCamera = (ImageButton) findViewById(R.id.homeBtnTakePic);
        
        sl.setOnShakeListener(this);
        loadingDialog = ProgressDialog.show(Home.this, "", "Loading...", true);
        btnCamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent imageCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				imageCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File("/tmp/newImage.bmp")));
				startActivityForResult(imageCapture, CAPTURE_CAMERA);
			}
		});
        imgSplash.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(self, PhotoActivity.class);
				i.putExtra("parcelled-photo", interestingPhotos.get(currentlyShown));
				startActivity(i);
			}
		});
        
        new LoadInterestingTask().execute();
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == CAPTURE_CAMERA) {
			super.onActivityResult(requestCode, resultCode, data);
			imgSplash.setImageBitmap(BitmapFactory.decodeFile("/tmp/newImage.bmp"));
    	}
	}

	@Override
	protected void onPause() {
		super.onPause();
		sl.stop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		sl.start();
	}

	public void onShake() {
		Vibrator vbr = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		vbr.vibrate(100);
		if (++randomCount > (interestingPhotos.size() - 20)) {
			new LoadInterestingTask().execute();
		} else {
			new FillSplashTask().execute((int)(Math.random()*100));
		}
	}
	
	class LoadInterestingTask extends AsyncTask<Integer, Void, Vector<Photo>> {
		@Override
		protected Vector<Photo> doInBackground(Integer... params) {
			Vector<Photo> photos = null;
			
			try {
	        	photos = flickr.getInterestingnessInterface().getPhotos();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return photos;
		}

		@Override
		protected void onPostExecute(Vector<Photo> result) {
			super.onPostExecute(result);
			
			if (result != null) {
				interestingPhotos = result;
				new FillSplashTask().execute((int)(Math.random()*100));
			} else {
				if (loadingDialog != null) {
					loadingDialog.cancel();
					loadingDialog = null;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
				builder.setMessage("An error has occurred. Please check network connectivity of Wi-Fi or Mobile Network.")
				       .setCancelable(false)
				       .setTitle("Attention")
				       .setNeutralButton("Exit", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                Home.this.finish();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}
	
	class FillSplashTask extends AsyncTask<Integer, Void, Void> {
		Bitmap fetchedPhoto;
		String caption = "";
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			sl.stop();
			if (loadingDialog == null) {
				Toast.makeText(Home.this, "Refreshing...", Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected Void doInBackground(Integer... params) {
			try {
				fetchedPhoto = interestingPhotos.get(params[0]).getBitmapPhoto();
				caption = interestingPhotos.get(params[0]).getPhotoSummary();
				currentlyShown = params[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			if (loadingDialog != null) {
				loadingDialog.cancel();
				loadingDialog = null;
			}
			
			if (fetchedPhoto != null) {
				imgSplash.setImageBitmap(fetchedPhoto);
				txtSummary.setText(caption);
				sl.start();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
				builder.setMessage("An error has occurred. Please check network connectivity of Wi-Fi or Mobile Network.")
				       .setCancelable(false)
				       .setTitle("Attention")
				       .setNeutralButton("Exit", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				                Home.this.finish();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}
}