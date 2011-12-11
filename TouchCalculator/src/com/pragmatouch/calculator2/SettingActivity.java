package com.pragmatouch.calculator2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pragmatouch.calculator2.R;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity implements SensorListener{

	SensorManager sensorMgr;
	long lastUpdate;
	float x,y,z,last_x,last_y,last_z;
	TextView m_txtProgress;
	int m_progress = 0;
		
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);		
		
		// register the sensor manager
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
		
		// screen fix
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// textview
		m_txtProgress = (TextView) findViewById(R.id.txtProgress);	
		
		
		// register the seekbar
		SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar1);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub				
			}
			
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				// If the value is 0, it means that it has no shaking
				// else you have to multiply with 100
				if(progress == 0)
					SecretManager.SHAKE_THRESHOLD = 0;
				else
					SecretManager.SHAKE_THRESHOLD =  1200 - (progress * 10);
				
				// show the progress ratings
				m_txtProgress.setText("민감도 : " + progress + "%");
				
				m_progress = progress;
			}
		});
		
		ReadSetting();
		
		m_txtProgress.setText("민감도 : " + m_progress + "%");
		seekBar.setProgress(m_progress);
	}
	
	
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		//Log.i("jdebug", "onStop");
		
		// save the value of the sensitive to the setting file
		boolean deleted = deleteFile("setting.dat");
		if(deleted == true) 
		{ 
			// Update the setting
			FileOutputStream fos;
			try {
				
				fos = openFileOutput("setting.dat", Context.MODE_WORLD_READABLE);
				String strProgress = m_progress + "";
            	fos.write(strProgress.getBytes());
            	fos.close();
            	
            	Log.i("jdebug", "setting.dat has been written");
            	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					
		}
	}

	void ReadSetting()
	{
		//passwd.dat 파일이 없으면 만든다.
		try {
			FileInputStream fis = openFileInput("setting.dat");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
			FileOutputStream fos;
			try {
				
				fos = openFileOutput("setting.dat", Context.MODE_WORLD_READABLE);
				String str = "0";
	        	fos.write(str.getBytes());
	        	fos.close();
	        	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String strSensitive = null;
		try {
			FileInputStream fis = openFileInput("setting.dat");
			byte[] data = new byte[fis.available()];
			while(fis.read(data) != -1){;}
			fis.close();
			strSensitive = new String(data);
			
			if(strSensitive.equals("") == true)
				strSensitive = "0";
			
			m_progress = Integer.valueOf(strSensitive);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void onAccuracyChanged(int sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	
	public void onSensorChanged(int sensor, float[] values) {
		// TODO Auto-generated method stub
		if(SecretManager.SHAKE_THRESHOLD != 0)
		{
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
				
				if(speed > SecretManager.SHAKE_THRESHOLD)
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
}
