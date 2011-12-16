package com.androidjava.app.icalculator;

import com.android.internal.telephony.ITelephony;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
/*
 *  call deny set
 */
public class DPhoneStateListener extends PhoneStateListener {
	 
	private TelephonyManager tm;
	private AudioManager     aManager;
	private int              mode;
	private ITelephony       telephonyService; 
	private Context          context; 
	
	public DPhoneStateListener(TelephonyManager tm, AudioManager  aManager, int mode, ITelephony telephonyService, Context context){
		this.tm       = tm;
		this.aManager = aManager;
		this.mode     = mode;
	    this.telephonyService = telephonyService;
	    this.context          = context;
		
	}
	   
	public void onCallStateChanged(int state, String  incommingNumber){

		
    
		// Call Start
		if(state == TelephonyManager.CALL_STATE_RINGING){
			Log.i(" Call Start", Integer.toString(TelephonyManager.CALL_STATE_RINGING));   // 1
			 
			try {
				
				if(telephonyService == null){
					
				}else{
					telephonyService.endCall();
				
				}   

			} catch (Exception e) {
				Log.e(this.toString() + " onCallStateChanged", e.toString());
			}   
                      
		}
		
		// Call End
		if(state == TelephonyManager.CALL_STATE_IDLE){
			Log.i("Call End", Integer.toString(TelephonyManager.CALL_STATE_IDLE));

			try {
				telephonyService = null;
				aManager.setRingerMode(mode);
				NotificationManager NM = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				NM.cancel(1);
			} catch (Exception e) {
				Log.e(this.toString() + " onCallStateChanged", e.toString());
			}

			
		}

	}
	

	
	
}
	

