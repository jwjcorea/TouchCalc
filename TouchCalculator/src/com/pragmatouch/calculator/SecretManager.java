package com.pragmatouch.calculator;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

public class SecretManager extends TabActivity {
	TabFactory factory;
	static TabHost m_tabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secretmanager);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, AddFilterActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost
				.newTabSpec("AddFilter")
				.setIndicator("사용자 추가",
						res.getDrawable(R.drawable.ic_tab_menu))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, ChangePwdActivity.class);
		spec = tabHost.newTabSpec("ChangePwd")
				.setIndicator("비밀번호 변경", res.getDrawable(R.drawable.ic_tab_menu))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, HowToActivity.class);
		spec = tabHost
				.newTabSpec("Howto")
				.setIndicator("도움말",
						res.getDrawable(R.drawable.ic_tab_menu))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(2);
		
		m_tabHost = tabHost;
	}
	
	class TabFactory implements TabHost.TabContentFactory{
		Context mCon;		
		
		public TabFactory(Context context) {
			// TODO Auto-generated constructor stub
			mCon = context;
		}

		@Override
		public View createTabContent(String tag) {
			// TODO Auto-generated method stub
			Log.i("jdebug", tag);
			return null;
		}		
	}
	
	static void external_selectTab(int nTab)
	{		
		m_tabHost.setCurrentTab(nTab);
	}
}
