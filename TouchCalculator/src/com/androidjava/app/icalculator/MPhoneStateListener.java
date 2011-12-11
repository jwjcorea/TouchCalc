package com.androidjava.app.icalculator;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;


public class MPhoneStateListener extends PhoneStateListener {
	 
	private TelephonyManager tm;
	private AudioManager  aManager;
	private int          mode;
	private ITelephony telephonyService; 
	
	public MPhoneStateListener(AudioManager aManager, int mode, ITelephony telephonyService){
		this.tm       = tm;
		this.aManager = aManager;
		this.mode    =  mode;
		this.telephonyService = telephonyService;
	} 
	
	public void onCallStateChanged(int state, String  incommingNumber){

		if(state == TelephonyManager.CALL_STATE_RINGING){


			try {

				if(telephonyService == null){
					
				}else{

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				//Log.i("ERROR", e.toString());
			}
		}

		
		if(state == TelephonyManager.CALL_STATE_IDLE){
			

			aManager.setRingerMode(mode);
			
			if(telephonyService == null){
				
			}else{

				try {
	
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.i("ERROR", e.toString());
				}

			}

		}
	}

}
