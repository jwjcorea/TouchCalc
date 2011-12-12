package com.androidjava.app.icalculator;

import java.lang.reflect.Method;
import java.util.logging.Logger;

import com.android.internal.telephony.ITelephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;


public class CustomBroadcastReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		

		Bundle b = intent.getExtras();

		String state = b.getString(TelephonyManager.EXTRA_STATE);

		if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

			String incommingNumber = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
			
			SQLiteOpenHelper dbHelper = new DBManager(context, "userList.db", null, 1);		
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			String where = " tel ='"+incommingNumber+"'";

			Cursor c  = db.query("userList", null , where, null, null, null, null);
			String  notsilent= "";
			String  notReceive ="";
			
			while(c.moveToNext()){
				notsilent =  c.getString(2);
				notReceive = c.getString(3);
			}
			c.close();
			
			ITelephony telephonyService = null;
			
			
			try{
				
				@SuppressWarnings("rawtypes")
				Class cl = Class.forName(tm.getClass().getName()); 
				Method m = cl.getDeclaredMethod("getITelephony"); 
				m.setAccessible(true); 
				telephonyService = (ITelephony)m.invoke(tm); 

			}catch(Exception e){
				//Log.i("ERROR", e.toString());
			}
			
			
			AudioManager  aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			int mode = aManager.getRingerMode();

			// 수신 거부일 경우 	
			if (notReceive.endsWith("1")){
				Log.i("수신거부 세팅입니다.", incommingNumber);  // 수신거부 세팅

				aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				DPhoneStateListener phoneStateListenerD = new DPhoneStateListener(tm, aManager, mode, telephonyService);
				tm.listen(phoneStateListenerD, phoneStateListenerD.LISTEN_CALL_STATE);
				       
				
			}else{

				if(notsilent.equals("1")){

					Log.i("무음 세팅입니다.", incommingNumber);  // 무음 세팅
					aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					MPhoneStateListener phoneStateListenerM = new MPhoneStateListener(aManager,  mode, telephonyService);
					tm.listen(phoneStateListenerM, phoneStateListenerM.LISTEN_CALL_STATE);
				
				}
			}
			
			
			Log.i("zzzzzzzzzzzzzzzzzzzzz", "zzzzzzzzzzzzzzzzz");  // 수신거부 세팅
			

    


			
			
     	   Log.i("YYYYYYYYYYYYYYYYYYYYY", "YYYYYYYYYYYYYYYYYYYYYYY");  // 수신거부 세팅

				


		}

	}
	
	
 



	





}

