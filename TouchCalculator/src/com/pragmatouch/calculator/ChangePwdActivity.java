package com.pragmatouch.calculator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePwdActivity extends Activity implements SensorListener{

	EditText editCurPwd;
	EditText editNewPwd;
	EditText editConfirmPwd;
	Button btnYes; 
	Button btnCancel;  
	SensorManager sensorMgr;
	long lastUpdate;
	float x,y,z,last_x,last_y,last_z;
	
	public InputFilter filterNum = new InputFilter() {
		
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			// TODO Auto-generated method stub
			Pattern ps = Pattern.compile("^[0-9]+$");
			if (!ps.matcher(source).matches()) {
				return "";
			}
			return null;
		}
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);
		
		// register the sensor manager
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
		
		// screen fix
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					
		editCurPwd = (EditText) findViewById(R.id.editCurPwd);
		editNewPwd = (EditText) findViewById(R.id.editNewPwd);
		editConfirmPwd = (EditText) findViewById(R.id.editConfirmNewPwd);
		btnYes = (Button) findViewById(R.id.btnYes);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		editCurPwd.setInputType(InputType.TYPE_CLASS_NUMBER);
		editNewPwd.setInputType(InputType.TYPE_CLASS_NUMBER);
		editConfirmPwd.setInputType(InputType.TYPE_CLASS_NUMBER);
		editCurPwd.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4)}); // limit the input				
		editNewPwd.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4)}); // limit the input
		editConfirmPwd.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4)}); // limit the input
		
		editCurPwd.addTextChangedListener(new TextWatcher() {
			
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				// only needed the num				
			}
			
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		btnYes.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					FileInputStream fis = openFileInput("passwd.dat");
					byte[] data = new byte[fis.available()];
					while(fis.read(data) != -1){;}
					fis.close();
					String strPasswd = new String(data);
					
					if(strPasswd.equals("") == true)
					{
						strPasswd = "0000";
					}
					
					// get the text of the editbox
					String strCurPwd = editCurPwd.getText().toString();
					String newPwd = editNewPwd.getText().toString();
					String conFirmNewPwd = editConfirmPwd.getText().toString();
					
					// check whether it is valid
					if(strCurPwd.equals("") == true)
					{
						// show the alert message
						Toast.makeText(ChangePwdActivity.this, "현재  패스워드를 넣어 주십시오.", 1).show();
						editCurPwd.setFocusable(true);
						return;
					}
					
					if(newPwd.equals("") == true)
					{
						// show the alert message
						Toast.makeText(ChangePwdActivity.this, "새로운 패스워드를 넣어 주십시오.", 1).show();
						editNewPwd.setFocusable(true);
						return;
					}
					
					if(conFirmNewPwd.equals("") == true)
					{
						// show the alert message
						Toast.makeText(ChangePwdActivity.this, "비밀번호 확인란에 입력이 빠졌습니다.", 1).show();
						editConfirmPwd.setFocusable(true);
						return;
					}
					
					// check the passwd with current passwd
					// get the text of the current editbox
					if(strPasswd.equals(editCurPwd.getText().toString()) == false)
					{
						// show the error message
						Toast.makeText(ChangePwdActivity.this, "현재 패스워드가 틀립니다. 다시 입력해 주십시오.", 1).show();						
						// then clear the value inputed
						editCurPwd.setText("");
						editNewPwd.setText("");
						editConfirmPwd.setText("");
						// set focus
						editCurPwd.setFocusable(true);
						
						return;
					}
					else // seccess
					{	
						if(newPwd.equals(conFirmNewPwd) == true)
						{							
							boolean deleted = deleteFile("passwd.dat");
							if(deleted == true)
							{
								// write the new passwe
								FileOutputStream fos = openFileOutput("passwd.dat", Context.MODE_WORLD_READABLE);				            	
				            	fos.write(newPwd.getBytes());
				            	fos.close();
								
				            	
								Toast.makeText(ChangePwdActivity.this, "패스워드가 변경되었습니다.", 1).show();
								
								editCurPwd.setText("");
								editNewPwd.setText("");
								editConfirmPwd.setText("");
								
								finish();
							}
						}
						else
						{
							Toast.makeText(ChangePwdActivity.this, "비밀번호 확인이 맞지 않습니다. 다시 입력해 주십시오", 1).show();
						
							editNewPwd.setText("");
							editConfirmPwd.setText("");
							editNewPwd.setFocusable(true);
						}
					}
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally
				{
					
				}				
			}
		});
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
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
}
