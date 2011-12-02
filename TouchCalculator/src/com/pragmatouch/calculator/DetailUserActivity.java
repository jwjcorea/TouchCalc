package com.pragmatouch.calculator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DetailUserActivity extends Activity {

	Button btnTel = null;
	Button btnMsg = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.detailuser);
				
		// get the control
		btnTel = (Button) findViewById(R.id.btnTel);
		btnMsg = (Button) findViewById(R.id.btnMessage);
		TextView tvName = (TextView) findViewById(R.id.textName);
		TextView tvTel = (TextView) findViewById(R.id.textTel);
				
		// set the user info
		// 1. get the info
		Intent i = getIntent();
		String strName = i.getStringExtra("name");
		String strTel = i.getStringExtra("tel");
		
		// 2. set the info
		tvName.setText(strName);
		tvTel.setText(strTel);
		
		// listener
		btnTel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri number = Uri.parse("tel:" + "010-2937-1864");
				Intent i = new Intent(Intent.ACTION_DIAL, number);
				startActivity(i);
			}
		});
		
		btnMsg.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(Intent.ACTION_SENDTO);
				i.setData(Uri.parse("smsto:" + "010-2937-1864"));
				startActivity(i);
			}
		});
	}
}
