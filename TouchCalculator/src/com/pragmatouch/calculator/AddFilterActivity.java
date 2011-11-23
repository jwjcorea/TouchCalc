package com.pragmatouch.calculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddFilterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		MyDialog dlg = new MyDialog(AddFilterActivity.this);
		dlg.show();
	}

	public class MyDialog extends Dialog
	{
		public MyDialog(Context context) {
			super(context);
			
			setTitle("사용자 추가");
			setContentView(R.layout.dialog_add_user);
			
			// TODO Auto-generated constructor stub
			Button btnTel = (Button) findViewById(R.id.btnTel);
			btnTel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Log.i("jdebug", "btnTel");
				}
			});
			
			Button btnDirect = (Button) findViewById(R.id.btnDirect);
			btnDirect.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hide();
					
					MyAddUserDialog dlg = new MyAddUserDialog(AddFilterActivity.this);
					dlg.show();
				}
			});
		}		
	}
	
	public class MyAddUserDialog extends Dialog
	{

		public MyAddUserDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
			
			setTitle("사용자 추가");
			setContentView(R.layout.dialog_direct_add_user);
			
			Button btnSave = (Button) findViewById(R.id.btnSave);
			Button btnCancel = (Button) findViewById(R.id.btnCancel);
			
			btnSave.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hide();
				}
			});
		}		
	}
}
