package com.pragmatouch.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
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
			String resultset = "";
			
			while(c.moveToNext()){
				Log.i("BBBBBBBBBBBBBBBBBBBB", c.getString(0));
				Log.i("CCCCCCCCCCCCCCCCCCCCC", c.getString(1));
				Log.i("CCCCCCCCCCCCCCCCCCCCC", c.getString(2)); // 公澜老 版快 1
				Log.i("CCCCCCCCCCCCCCCCCCCCC", c.getString(3));  // 荐脚芭何老 版快 1
				
				resultset =  c.getString(3);
			}

			db.close();
			
			 
			  
			// 公澜老 版快 
			if(resultset.equals("1")){
				Log.i("999999999999999999999", resultset);  // 荐脚芭何老 版快 1
				




				
			}



			

		}

	}
	
	





}

