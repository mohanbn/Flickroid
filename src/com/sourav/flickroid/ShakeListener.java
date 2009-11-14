package com.sourav.flickroid;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeListener implements SensorEventListener {
	private static final double SCALAR_VEL_THRESHOLD = 20;
	private static final int SHAKE_TIMEOUT = 500;
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 3;

	private SensorManager sensorMgr;
	private Sensor accelerometer;
	private ArrayList<OnShakeListener> shakeListeners;

	private long mLastTime;
	private int mShakeCount = 0;
	private long mLastShake;

	public interface OnShakeListener {
		public void onShake();
	}

	public ShakeListener(Context context) {
		sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		accelerometer = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
		shakeListeners = new ArrayList<OnShakeListener>();
	}

	public void setOnShakeListener(OnShakeListener listener) {
		shakeListeners.add(listener);
	}

	public void start() {
		if (accelerometer != null) {
			sensorMgr.registerListener(this, accelerometer,
					SensorManager.SENSOR_DELAY_GAME);
		} else {
			throw new UnsupportedOperationException("Accelerometer not supported");
		}
	}

	public void stop() {
		if (accelerometer != null) {
			sensorMgr.unregisterListener(this, accelerometer);
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Nothing to do here.

	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			long now = System.currentTimeMillis();
			
			if ((now - mLastTime) > SHAKE_TIMEOUT) {
				mShakeCount = 0;
			}
	
			double scalarVelocity = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
			
			if (scalarVelocity > SCALAR_VEL_THRESHOLD) {
				if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
					mLastShake = now;
					mShakeCount = 0;
					if (shakeListeners.size() > 0) {
						for (OnShakeListener listener : shakeListeners) {
							listener.onShake();
						}
					}
				}
				
				mLastTime = now;
			}
		}
	}
}
