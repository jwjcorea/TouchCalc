package com.pragmatouch.calculator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DetailUserActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.detailuser);
		
		Button btnTel = (Button) findViewById(R.id.btnTel);
		btnTel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri number = Uri.parse("tel:" + "010-2937-1864");
				Intent i = new Intent(Intent.ACTION_DIAL, number);
				startActivity(i);
			}
		});
		
		Button btnMsg = (Button) findViewById(R.id.btnMessage);
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
