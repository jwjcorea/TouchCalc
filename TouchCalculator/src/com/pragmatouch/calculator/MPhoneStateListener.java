package com.pragmatouch.calculator;

import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MPhoneStateListener extends PhoneStateListener {
	
	private AudioManager  aManager;
	private int          mode;
	
	public MPhoneStateListener(AudioManager aManager, int mode){
		this.aManager = aManager;
		this.mode    =  mode;
	}
	
	public void onCallStateChanged(int state, String  incommingNumber){
		
		Log.i("AAAAAAAAAA","AAAAAAAAAAAAAA");
		
		if(state == TelephonyManager.CALL_STATE_RINGING){

		}
		
		if(state == TelephonyManager.CALL_STATE_IDLE){
			
			Log.i("CCCCCCCCCCCCCC","CCCCCCCCCCCCCC");
			aManager.setRingerMode(mode);

		}
	}

}
