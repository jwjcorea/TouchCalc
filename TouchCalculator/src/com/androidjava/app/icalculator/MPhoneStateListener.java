package com.androidjava.app.icalculator;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/*
 *  call mute set
 */
public class MPhoneStateListener extends PhoneStateListener {
	 
	private TelephonyManager tm;
	private AudioManager     aManager;
	private int              mode;
	private ITelephony       telephonyService; 
	private Context          context; 
	
	/*
	 * call Mute set
	 */
	public MPhoneStateListener(AudioManager aManager, int mode, ITelephony telephonyService, Context context){
		this.tm               = tm;
		this.aManager         = aManager;
		this.mode             =  mode;
		this.telephonyService = telephonyService;
		this.context          = context;
	} 
	
	public void onCallStateChanged(int state, String  incommingNumber){

		if(state == TelephonyManager.CALL_STATE_RINGING){
	        Log.i("Call Start", Integer.toString(TelephonyManager.CALL_STATE_RINGING));
			
			try {

			} catch (Exception e) {
				Log.e(this.toString() + " onCallStateChanged", e.toString());
			}
		}

		
		if(state == TelephonyManager.CALL_STATE_IDLE){
			 Log.i("Call End", Integer.toString(TelephonyManager.CALL_STATE_IDLE));

			try {
				
				aManager.setRingerMode(mode);
				NotificationManager NM = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
				NM.cancel(0);

			} catch (Exception e) {
				Log.e(this.toString() + " onCallStateChanged", e.toString());
			}
			

		}
	}

}
