package com.pragmatouch.calculator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePwdActivity extends Activity {

	EditText editCurPwd;
	EditText editNewPwd;
	EditText editConfirmPwd;
	Button btnYes;
	Button btnCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password);
		
		editCurPwd = (EditText) findViewById(R.id.editCurPwd);
		editNewPwd = (EditText) findViewById(R.id.editNewPwd);
		editConfirmPwd = (EditText) findViewById(R.id.editConfirmNewPwd);
		btnYes = (Button) findViewById(R.id.btnYes);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		btnYes.setOnClickListener(new View.OnClickListener() {
			
			@Override
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
						Toast.makeText(ChangePwdActivity.this, "����  �н����带 �־� �ֽʽÿ�.", 1).show();
						editCurPwd.setFocusable(true);
						return;
					}
					
					if(newPwd.equals("") == true)
					{
						// show the alert message
						Toast.makeText(ChangePwdActivity.this, "���ο� �н����带 �־� �ֽʽÿ�.", 1).show();
						editNewPwd.setFocusable(true);
						return;
					}
					
					if(conFirmNewPwd.equals("") == true)
					{
						// show the alert message
						Toast.makeText(ChangePwdActivity.this, "��й�ȣ Ȯ�ζ��� �Է��� �������ϴ�.", 1).show();
						editConfirmPwd.setFocusable(true);
						return;
					}
					
					// check the passwd with current passwd
					// get the text of the current editbox
					if(strPasswd.equals(editCurPwd.getText().toString()) == false)
					{
						// show the error message
						Toast.makeText(ChangePwdActivity.this, "���� �н����尡 Ʋ���ϴ�. �ٽ� �Է��� �ֽʽÿ�.", 1).show();						
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
								
				            	
								Toast.makeText(ChangePwdActivity.this, "�н����尡 ����Ǿ����ϴ�.", 1).show();
								
								editCurPwd.setText("");
								editNewPwd.setText("");
								editConfirmPwd.setText("");
								
								SecretManager.external_selectTab(0);
							}
						}
						else
						{
							Toast.makeText(ChangePwdActivity.this, "��й�ȣ Ȯ���� ���� �ʽ��ϴ�. �ٽ� �Է��� �ֽʽÿ�", 1).show();
						
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
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SecretManager.external_selectTab(0);
			}
		});
	}
}