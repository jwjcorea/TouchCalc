package com.pragmatouch.calculator;

import android.app.Activity;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class HelpActivity extends Activity implements SensorListener{

	SensorManager sensorMgr;
	long lastUpdate;
	float x,y,z,last_x,last_y,last_z;
	
	private static final int SHAKE_THRESHOLD = 500;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);		
		
		// register the sensor manager
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);			
	}
	
	
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
			long curTime = System.currentTimeMillis();
			// only allow one update every 100ms.
			if ((curTime - lastUpdate) > 100) {
			long diffTime = (curTime - lastUpdate);
			lastUpdate = curTime;

			x = values[SensorManager.DATA_X];
			y = values[SensorManager.DATA_Y];
			z = values[SensorManager.DATA_Z];

			float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
			
			if(speed > SHAKE_THRESHOLD)
			{
				//Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
				finish();
			}

			last_x = x;
			last_y = y;
			last_z = z;
			}
		}
	}
}
