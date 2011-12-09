package com.pragmatouch.calculator;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class DetailUserActivity extends Activity implements SensorListener{

	Button btnTel = null;
	Button btnMsg = null;
	String m_strName;
	String m_strTel;
	SensorManager sensorMgr;
	long lastUpdate;
	float x,y,z,last_x,last_y,last_z;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		
		super.onCreate(savedInstanceState);
		
		


		  
		  
		setContentView(R.layout.detailuser);
		
		// register the sensor manager
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
		
		// screen fix
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				
		// get the control
		btnTel = (Button) findViewById(R.id.btnTel);
		btnMsg = (Button) findViewById(R.id.btnMessage);
		TextView tvName = (TextView) findViewById(R.id.textName);
		TextView tvTel = (TextView) findViewById(R.id.textTel);
				
		// set the user info
		// 1. get the info
		Intent i = getIntent();
		m_strName = i.getStringExtra("name");
		m_strTel = i.getStringExtra("tel");
		
		// 2. set the info
		tvName.setText(m_strName); 
		tvTel.setText(m_strTel);
		
		Log.i("================================>","111");
		//phoneBookControl(m_strTel, true);  // 리스트 보기 
		phoneBookControl(m_strTel, false); // 삭제하기 
		
		Log.i("================================>",CallLog.Calls.NUMBER);


		
		// listener
		btnTel.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri number = Uri.parse("tel:" + m_strTel);
				Intent i = new Intent(Intent.ACTION_DIAL, number);
				startActivity(i);
			}
		});
		
		btnMsg.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_SENDTO);
				i.setData(Uri.parse("smsto:" + m_strTel));
				startActivity(i);
			}
		});
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

	    public void phoneBookControl(String phoneNumber, boolean type) {
	    	
            int callcount = 0;
            String callname = "";
            String calltype = "";
            String calllog = "";
	    	
	    	Cursor curCallLog = getCallHistoryCursor(this, phoneNumber);
	    	
	    	
            if (curCallLog.moveToFirst() && curCallLog.getCount() > 0) {
                while (curCallLog.isAfterLast() == false) {
                	
                	StringBuffer sb = new StringBuffer();

                	
                	if(type){
                        if (curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.TYPE)).equals(MESSAGE_TYPE_INBOX)){
                            calltype = "수신";
                        }
                        else if (curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.TYPE)).equals(MESSAGE_TYPE_SENT)){
                            calltype = "발신";                    
                        }
                        else if (curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.TYPE)).equals(MESSAGE_TYPE_CONVERSATIONS)){
                            calltype = "부재중";                    
                        }
                        
                        if (curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.CACHED_NAME)) == null) {
                            callname = "NoName";
                        }
                        else {
                            callname = curCallLog.getString(curCallLog
                                    .getColumnIndex(CallLog.Calls.CACHED_NAME));
                        }
                        sb.append(timeToString(curCallLog.getLong(curCallLog
                                .getColumnIndex(CallLog.Calls.DATE))));
                        sb.append("\t").append(calltype);
                        sb.append("\t").append(callname);
                        sb.append("\t").append(curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.NUMBER)));
                    		            
                        curCallLog.moveToNext();
                        callcount++;
                        Log.i("call history[", sb.toString());
                	}else{
                		
                		
                		
                		getBaseContext().getContentResolver().delete(CallLog.Calls.CONTENT_URI, " number = '"+phoneNumber+"'",null);
                        curCallLog.moveToNext();
                        callcount++;
                	}
                	

                    
                    
                    
                    
                    
                    
                    
                }  // while end
            }
	    	
	    }
	    
	    
	    
	    private String timeToString(Long time) {
	        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String date = simpleFormat.format(new Date(time));
	        return date;
	    }
	    
	    
	    
	    final static private String[] CALL_PROJECTION = { CallLog.Calls.TYPE,
            CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER,
            CallLog.Calls.DATE, CallLog.Calls.DURATION };

	    public static final String MESSAGE_TYPE_INBOX = "1";
	    public static final String MESSAGE_TYPE_SENT = "2";
	    public static final String MESSAGE_TYPE_CONVERSATIONS = "3";
	    public static final String MESSAGE_TYPE_NEW = "new";

	    private Cursor getCallHistoryCursor(Context context, String  phoneNumber) {
	        Cursor cursor = context.getContentResolver().query(
	                CallLog.Calls.CONTENT_URI, CALL_PROJECTION,
	                " number = '"+phoneNumber+"'", null, CallLog.Calls.DEFAULT_SORT_ORDER);
	        
	        return cursor;
	    }
}
