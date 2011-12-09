package com.pragmatouch.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.AudioManager;
import android.os.Bundle;
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
			db.close();
			
			 
			  
			// 무음일 경우 
			if(notsilent.equals("1")){
				Log.i("무음 세팅입니다.", incommingNumber);  // 무음 세팅
				AudioManager  aManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				int mode = aManager.getRingerMode();
				aManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

				  
				MPhoneStateListener phoneStateListener = new MPhoneStateListener(aManager,  mode);
				tm.listen(phoneStateListener, phoneStateListener.LISTEN_CALL_STATE);

				


			// 수신 거부일 경우 	
			}else if(notReceive.endsWith("1")){
				Log.i("수신거부 세팅입니다.", incommingNumber);  // 무음 세팅
				
				
			}


		}

	}


	





}

