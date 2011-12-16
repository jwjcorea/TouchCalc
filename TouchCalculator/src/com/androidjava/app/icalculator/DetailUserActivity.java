package com.androidjava.app.icalculator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
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
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
	
	
	String hardWare ="";
	
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
		m_historyList.setOnItemClickListener(mItemClickListener);
		
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
				
				
				// 갤럭시 S 이면 
				if(isGalaxyS()){
					//Log.i("INFO",  "This is GalaxyS");
					hardWare = "GALAXYS";
					UpdateSMSListGalaxyS();
				}
				else{
					//Log.i("INFO",  "This is not GalaxyS");
					UpdateSMSList();
				}
				
				
				
				
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
					
					if(hardWare.equals("GALAXYS")){
						msBookControlGalaxyS(m_strTel, false);   // 문자 내역을 지우고
						UpdateSMSListGalaxyS();					
						break;	
					}
					else {
						msBookControl(m_strTel, false);   // 문자 내역을 지우고
						UpdateSMSList();					
						break;	
						
					}
			
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
	
	void UpdateSMSListGalaxyS()
	{
		ClearHistoryList();
		msBookControlGalaxyS(m_strTel, true);
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
	    	
	    	try{
	            int callcount = 0;
	            String callname = "";
	            int nCallType = 0;
	            String calllog = "";
		    	
		    	Cursor curCallLog = getCallHistoryCursor(this, phoneNumber);
	            if (curCallLog.moveToFirst() && curCallLog.getCount() > 0) {
	            	if(type){ // Call Search
	                    while (curCallLog.isAfterLast() == false) {
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
	            
	                        curCallLog.moveToNext();
	                        callcount++;

	                    }  // while end
	                    curCallLog.close();
	            	}else{ //Call Delete
	            		getBaseContext().getContentResolver().delete(CallLog.Calls.CONTENT_URI, " number = '"+phoneNumber+"'",null);
	            	}
	            }

	    	}catch(Exception e){
	    		Log.e(this.toString() + " msBookControl", e.toString());
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

	    	try{
	            Uri allMessage = Uri.parse("content://sms/");  
	            Cursor cur = this.getContentResolver().query(allMessage, null, " address = '"+phoneNumber+"'" , null, null);


	            String row = "";
	            String msg = "";
	            String date = "";
	            String protocol = "";
	            
	            if(type){ // SMS Search
		            while (cur.moveToNext()) {
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
		
	                    MyHistoryItem historyItem = new MyHistoryItem();
	                    historyItem.nCallType = 3;	// 문자이미지.
	                    historyItem.strHistory = msg;
	                    m_arryHistoryItems.add(historyItem);
		            }
		            cur.close();
	            }else{  // SMS Delete
            	    getBaseContext().getContentResolver().delete(allMessage,  " address = '"+phoneNumber+"'",  null );
            }
	    		
	    	}catch(Exception e){
	    		Log.e(this.toString() + " msBookControl", e.toString());
	    	}
	    	

	    }
	    
	    
	    public boolean isGalaxyS(){
	    	
            Uri allMessage = null;
            Cursor cur     = null;
            
            try{
            	 allMessage = Uri.parse("content://com.sec.mms.provider/message");  
            	 cur  = this.getContentResolver().query(allMessage, null, null , null, null);
            	
            }catch(Exception e){
            	Log.e("isGalaxyS", e.toString());
            	return false;
            }

            if(cur == null){
            	return false;
            }else{
                cur.close();
                return true;
            }
	    }
	    
	    
	    
	    
	    public void msBookControlGalaxyS(String phoneNumber, boolean type){
	    	
            Uri allMessage = Uri.parse("content://com.sec.mms.provider/message");  
            Cursor cur = this.getContentResolver().query(allMessage, null, " MDN1st = '"+phoneNumber+"' OR MDN2nd ='"+phoneNumber+"'" , null, null);
            int count = cur.getCount();
            Long  ldate;
            String row = "";
            String msg = "";
            String date = "";
            String protocol = "";
            
            
            if(type){ // SMS SEARCH
                while (cur.moveToNext()) {
                	ldate = cur.getLong(1);

                    MyHistoryItem historyItem = new MyHistoryItem();
                    historyItem.nCallType = 3;	// 문자이미지.
                    historyItem.strHistory =timeToString(Long.valueOf(ldate));
                    m_arryHistoryItems.add(historyItem);
                }              
                cur.close();
            }
            else{  // SMS DELETE
            	try{
            		getBaseContext().getContentResolver().delete(allMessage,  " MDN1st = '"+phoneNumber+"' OR MDN2nd ='"+phoneNumber+"'",  null );
            	}catch(Exception e){
            		Log.e(this.toString() + "msBookControlGalaxyS", e.toString());
            	}
            }    

	}
	    
	public static String parserPhoneNumber(String phone){
		
		int length = phone.length();
		String customer;
		
		if(length == 11){
			
			customer = phone.substring(0,3);
			customer = customer + "-";
			customer = customer + phone.substring(3,7);
			customer = customer + "-";
			customer = customer + phone.substring(7,11);
			return customer;
		}
		
		if(length == 10){
			customer = phone.substring(0,3);
			customer = customer + "-";
			customer = customer + phone.substring(3,6);
			customer = customer + "-";
			customer = customer + phone.substring(6,10);
			return customer;
		}
		
		if(length == 10){
			customer = phone.substring(0,3);
			customer = customer + "-";
			customer = customer + phone.substring(3,6);
			customer = customer + "-";
			customer = customer + phone.substring(6,10);
			return customer;
		}
		
		if(length == 9){
			customer = phone.substring(0,2);
			customer = customer + "-";
			customer = customer + phone.substring(2,5);
			customer = customer + "-";
			customer = customer + phone.substring(5,9);
			return customer;
		}
		
		if(length == 8){
			customer = phone.substring(0,4);
			customer = customer + "-";
			customer = customer + phone.substring(4,8);
			return customer;
		}
		
		return "";
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
	    
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		
		public void onItemClick(AdapterView parnet, View view, int position, long id) {
			// TODO Auto-generated method stub
			
			MyHistoryItem item = m_arryHistoryItems.get(position);
			if(item.strHistory.equals("") == false)
			{
				SMSDialog smsDlg = new SMSDialog(DetailUserActivity.this);
				smsDlg.setText(item.strHistory);				
				smsDlg.show();
			}
		}
	};
	
	public class SMSDialog extends Dialog {
		Button btnExit;
		TextView txtSMS;
		
		public SMSDialog(Context context) {
			super(context);			
			
			requestWindowFeature(Window.FEATURE_NO_TITLE);
				
			// * set the dialog			
			setContentView(R.layout.dialog_sms);
			
			
			// * get the button
			btnExit = (Button) findViewById(R.id.btnExit);
			txtSMS = (TextView) findViewById(R.id.txtSMS);

			// * click 
			btnExit.setOnClickListener(new View.OnClickListener() {
				

				public void onClick(View v) {
					dismiss();
				}
			});
		}
		
		void setText(String strSMS)
		{
			// * insert the content to the textview
			txtSMS.setText(strSMS);
		}
	}
}
