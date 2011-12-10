package com.pragmatouch.calculator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.pragmatouch.calculator.SecretManager.MyItem;
import com.pragmatouch.calculator.SecretManager.MyListAdapter;
import com.pragmatouch.calculator.SecretManager.MyPopupListAdapter.ViewHolder;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class DetailUserActivity extends Activity implements SensorListener{

	Button m_btnTel = null;
	Button m_btnMsg = null;
	Button m_btnShowTelList = null;
	Button m_btnShowSMSList = null;
	Button m_btnDeleteHistory = null;
	ListView m_historyList = null;
	MyHistoryListAdapter m_myHistoryAdapter = null;
	ArrayList<MyHistoryItem> m_arryHistoryItems = null;
	int m_nActivedList = 0;		// If the value is 0, the tel history has showed. Otherwise 0 is the message history
	
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
		m_btnTel = (Button) findViewById(R.id.btnTel);
		m_btnMsg = (Button) findViewById(R.id.btnMessage);
		m_btnShowTelList = (Button) findViewById(R.id.btnShowTelList);
		m_btnShowSMSList = (Button) findViewById(R.id.btnShowMsgList);
		m_btnDeleteHistory = (Button) findViewById(R.id.btnDelList);
		
		TextView tvName = (TextView) findViewById(R.id.textName);
		TextView tvTel = (TextView) findViewById(R.id.textTel);	
		
		m_historyList = (ListView) findViewById(R.id.historyList);
				
		// * Set the user info
		// 1. get the info
		Intent i = getIntent();
		m_strName = i.getStringExtra("name");
		m_strTel = i.getStringExtra("tel");
		
		// 2. set the info
		tvName.setText(m_strName); 
		tvTel.setText(m_strTel);
		
		// 3. setting the history list
		m_arryHistoryItems = new ArrayList<MyHistoryItem>();
		m_myHistoryAdapter = new MyHistoryListAdapter(this, R.layout.listitem_history, m_arryHistoryItems);
		m_historyList.setAdapter(m_myHistoryAdapter);
		
		// * Init
		// 1. update list
		UpdateHistoryList();
				
		// listeners
		// 1. call button was pressed
		m_btnTel.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri number = Uri.parse("tel:" + m_strTel);
				Intent i = new Intent(Intent.ACTION_DIAL, number);
				startActivity(i);
			}
		});
		
		// 2. message button was pressed
		m_btnMsg.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_SENDTO);
				i.setData(Uri.parse("smsto:" + m_strTel));
				startActivity(i);
			}
		});
		
		// 3. tel history button was pressed
		m_btnShowTelList.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UpdateHistoryList();				
				m_nActivedList = 0;	// If the value is 0, the tel history has showed
			}
		});
		
		// 4. message history button was pressed
		m_btnShowSMSList.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				UpdateSMSList();
				m_nActivedList = 1;	// If the value is 0, the message history has showed
			}
		});
		
		// 5. Delete button was pressed
		m_btnDeleteHistory.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(m_nActivedList)
				{
				case 0:					
					phoneBookControl(m_strTel, false);	// Delete the tel history from the main
					UpdateHistoryList();
					break;
					
				case 1:					
					msBookControl(m_strTel, false);   // 문자 내역을 지우고
					UpdateSMSList();					
					break;				
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		UpdateHistoryList();
	}

	void ClearHistoryList()
	{
		int nSize = m_arryHistoryItems.size();
		for(int i=0; i<nSize; ++i)
			m_arryHistoryItems.remove(0);
	}
	
	void UpdateHistoryList()
	{
		ClearHistoryList();
		phoneBookControl(m_strTel, true);  // 리스트 보기
		m_myHistoryAdapter.notifyDataSetChanged();
	}
	
	void UpdateSMSList()
	{
		ClearHistoryList();
		msBookControl(m_strTel, true);
		m_myHistoryAdapter.notifyDataSetChanged();
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
            int nCallType = 0;
            String calllog = "";
	    	
	    	Cursor curCallLog = getCallHistoryCursor(this, phoneNumber);
	    		    	
            if (curCallLog.moveToFirst() && curCallLog.getCount() > 0) {
                while (curCallLog.isAfterLast() == false) {
                	
                	StringBuffer sb = new StringBuffer();

                	
                	if(type){
                        if (curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.TYPE)).equals(MESSAGE_TYPE_INBOX)){
                        	nCallType = 0; //"수신";
                        }
                        else if (curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.TYPE)).equals(MESSAGE_TYPE_SENT)){
                        	nCallType = 1; //"발신";                    
                        }
                        else if (curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.TYPE)).equals(MESSAGE_TYPE_CONVERSATIONS)){
                        	nCallType = 2; //"부재중";                    
                        }
                        
                        if (curCallLog.getString(curCallLog
                                .getColumnIndex(CallLog.Calls.CACHED_NAME)) == null) {
                            callname = "NoName";
                        }
                        else {
                            callname = curCallLog.getString(curCallLog
                                    .getColumnIndex(CallLog.Calls.CACHED_NAME));
                        }
                                               
                        // Insert the item to the array referenced to the history-list
                        MyHistoryItem historyItem = new MyHistoryItem();
                        historyItem.nCallType = nCallType;
                        historyItem.strHistory = timeToString(curCallLog.getLong(curCallLog.getColumnIndex(CallLog.Calls.DATE)));
                        m_arryHistoryItems.add(historyItem);
                        
                        //sb.append(timeToString(curCallLog.getLong(curCallLog
                        //      .getColumnIndex(CallLog.Calls.DATE))));
                        //sb.append("\t").append(calltype);
                        //sb.append("\t").append(callname);
                        //sb.append("\t").append(curCallLog.getString(curCallLog
                        //        .getColumnIndex(CallLog.Calls.NUMBER)));
                    		            
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
	    
	    
	    
	    
	    public void msBookControl(String phoneNumber, boolean type){
	    	

	    		
	            Uri allMessage = Uri.parse("content://sms/");  
	            Cursor cur = this.getContentResolver().query(allMessage, null, " address = '"+phoneNumber+"'" , null, null);
	            int count = cur.getCount();
	            Log.i( "AAAAAA" , "SMS count = " + count);
	            String row = "";
	            String msg = "";
	            String date = "";
	            String protocol = "";
	            while (cur.moveToNext()) {
	            	
	            	
	            if(type){ // 문자 조회이면
	                row = cur.getString(cur.getColumnIndex("address"));
	                msg = cur.getString(cur.getColumnIndex("body"));
	                date = cur.getString(cur.getColumnIndex("date"));
	                protocol = cur.getString(cur.getColumnIndex("protocol"));
	                // Logger.d( TAG , "SMS PROTOCOL = " + protocol);  
	                
	                String type2 = "";
	                if (protocol == MESSAGE_TYPE_SENT) type2 = "sent";
	                else if (protocol == MESSAGE_TYPE_INBOX) type2 = "receive";
	                else if (protocol == MESSAGE_TYPE_CONVERSATIONS) type2 = "conversations"; 
	                else if (protocol == null) type2 = "send"; 
	
	                Log.i( "AAAAAA" , "SMS Phone: " + row + " / Mesg: " + msg + " / Type: " + type + " / Date: " + timeToString(Long.valueOf(date)));
	                
	                // Insert the item to the array referenced to the history-list
                    MyHistoryItem historyItem = new MyHistoryItem();
                    historyItem.nCallType = 3;	// 문자이미지.
                    historyItem.strHistory = timeToString(Long.valueOf(cur.getColumnIndex("date")));
                    m_arryHistoryItems.add(historyItem);
	                
	            }else{  // 삭제이면 
	            	getBaseContext().getContentResolver().delete(allMessage,  " address = '"+phoneNumber+"'",  null );
	            }
            }
	    }
	    
	    
	    
	    
	    
	    
	    
	    // MyHistoryItem
	    class MyHistoryItem
	    {
	    	int nCallType;
	    	String strHistory;
	    }
	    
		// define class the MyHistoryListAdapter
	    class MyHistoryListAdapter extends BaseAdapter {

		Context mainCon;
		LayoutInflater Inflater;
		ArrayList<MyHistoryItem> arrySrc;
		int layout;
		int m_nPos;
	
		public MyHistoryListAdapter(Context context, int _layout,
				ArrayList<MyHistoryItem> _arrySrc) {
			mainCon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arrySrc = _arrySrc;
			layout = _layout;			
		}
		

		
		public int getCount() {
			return arrySrc.size();
		}

		
		public Object getItem(int position) {
			return arrySrc.get(position).strHistory;
		}

		
		public long getItemId(int position) {
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {			
			// TODO Auto-generated method stub
			final ViewHolder holder;			
			final int pos = position;
			
			if (convertView == null)
			{
				convertView = Inflater.inflate(layout, parent, false);
				holder = new ViewHolder();
				holder.imgHistory = (ImageView) convertView.findViewById(R.id.imgHistory);
				holder.txtHistory = (TextView) convertView.findViewById(R.id.txtHistory);
				convertView.setTag(holder);
			}
			else
				holder = (ViewHolder)convertView.getTag();
			
			// Setting the control
			// 1. Setting the image
			int nIdxImg = arrySrc.get(position).nCallType;
			switch(nIdxImg)
			{
			case 0:	// 수신
				holder.imgHistory.setImageResource(R.drawable.sym_call_incoming);
				break;
			case 1: // 발신
				holder.imgHistory.setImageResource(R.drawable.sym_call_outgoing);
				break;				
			case 2:	// 부재중
				holder.imgHistory.setImageResource(R.drawable.sym_call_missed);
				break;				
			case 3: // 문자
				holder.imgHistory.setImageResource(R.drawable.sym_action_email);
				break;
			}
			
			// 2. Setting the text	
			holder.txtHistory.setText(arrySrc.get(position).strHistory);
		
			return convertView;
		}
		
		protected class ViewHolder{
			protected ImageView imgHistory;
			protected TextView txtHistory;
		}
	}
}
