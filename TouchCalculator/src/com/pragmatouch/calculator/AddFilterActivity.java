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
					Log.i("jdebug", "btnDirect");					
				}
			});
		}		
	}
}
