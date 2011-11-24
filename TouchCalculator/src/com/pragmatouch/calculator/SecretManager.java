package com.pragmatouch.calculator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SecretManager extends Activity {
	ListView userList;
	ArrayList<MyItem> arryItem;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
		
		MenuItem item = menu.add(0, 1, 0, "사용자 추가");
		item.setIcon(R.drawable.icon);
		item.setAlphabeticShortcut('a');
		menu.add(0, 2, 0, "비밀번호 변경").setIcon(R.drawable.icon);
		menu.add(0, 3, 0, "도움말").setIcon(R.drawable.icon);		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent i = new Intent();
		
		switch(item.getItemId())
		{
		case 1:
			Log.i("jdebug", "add user");
			AddUser();
			return true;
			
		case 2:
			Log.i("jdebug", "password");
			i.setClass(this, ChangePwdActivity.class);
			startActivity(i);
			return true;
			
		case 3:
			Log.i("jdebug", "help");
			i.setClass(this, HelpActivity.class);
			startActivity(i);
			return true;
		}
		
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secretmanager);
		
		arryItem = new ArrayList<MyItem>();
		MyItem mi;
		mi = new MyItem("장준혁", "010-2937-1864", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("임형주", "010-0000-1111", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("박승철", "010-0000-2222", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("노용경", "010-0000-3333", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("장준혁", "010-2937-1864", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("임형주", "010-0000-1111", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("박승철", "010-0000-2222", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("노용경", "010-0000-3333", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("장준혁", "010-2937-1864", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("임형주", "010-0000-1111", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("박승철", "010-0000-2222", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("노용경", "010-0000-3333", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("장준혁", "010-2937-1864", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("임형주", "010-0000-1111", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("박승철", "010-0000-2222", 0, 0, 0);
		arryItem.add(mi);
		mi = new MyItem("노용경", "010-0000-3333", 0, 0, 0);
		arryItem.add(mi);
		
		MyListAdapter MyAdapter = new MyListAdapter(this, R.layout.listitem, arryItem);
		
		userList = (ListView) findViewById(R.id.userList);
		userList.setAdapter(MyAdapter);	
		
		userList.setOnItemClickListener(mItemClickListener);
		registerForContextMenu(userList);
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.userList)
		{			
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
			menu.setHeaderTitle("장준혁");			
			String[] menuItems = getResources().getStringArray(R.array.menu);
			for(int i=0; i<menuItems.length; ++i)
				menu.add(Menu.NONE, i, i, menuItems[i]);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIdx = item.getItemId();
		String[] menuItems = getResources().getStringArray(R.array.menu);
		String menuItemName = menuItems[menuItemIdx];
		
		Toast.makeText(SecretManager.this, info.position + "번째" + menuItemName + "기능 시작", 0).show();
		
		return true;
	}

	void AddUser()
	{
		MyDialog dlg = new MyDialog(this);
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
					
					MyAddUserDialog dlg = new MyAddUserDialog(SecretManager.this);
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
	
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView parnet, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String mes;
			mes = "Select Item = " + position;
			Toast.makeText(SecretManager.this, mes, 0).show();
			
		}	};
		
	class MyItem
	{
		MyItem(String _strName, String _strTel, int _bMute, int _bReceive, int _bRestore)
		{
			strName = _strName;
			strTel = _strTel;
			bMute = _bMute;
			bReceive = _bReceive;
			bReceive = _bRestore;
		}
		
		String strName;
		String strTel;
		int bMute;
		int bReceive;
		int bRestore;
	}
	
	// the adapter class
	class MyListAdapter extends BaseAdapter{

		Context mainCon;
		LayoutInflater Inflater;
		ArrayList<MyItem> arrySrc;
		int layout;
		
		public MyListAdapter(Context context, int _layout, ArrayList<MyItem> _arrySrc)
		{
			mainCon = context;
			Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arrySrc = _arrySrc;
			layout = _layout;
		}
		
		@Override
		public int getCount() {
			return arrySrc.size(); 
		}

		@Override
		public Object getItem(int position) {
			return arrySrc.get(position).strName;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int pos = position;
			if(convertView == null)
				convertView = Inflater.inflate(layout, parent, false);
			
			// text
			TextView txtName = (TextView)convertView.findViewById(R.id.textName);			
			TextView txtTel = (TextView)convertView.findViewById(R.id.textTel);
			
			txtName.setText(arrySrc.get(position).strName);
			txtTel.setText(arrySrc.get(position).strTel);
			
			// buttons
			Button btnMute = (Button) convertView.findViewById(R.id.btnMute);
			Button btnReceive = (Button) convertView.findViewById(R.id.btnReceive);
			Button btnRestore = (Button) convertView.findViewById(R.id.btnRestore);
			
			btnMute.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(SecretManager.this, "무음설정 버튼 눌림", 0).show();
				}
			});

			btnReceive.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(SecretManager.this, "수신거부 버튼 눌림", 0).show();
				}
			});

			btnRestore.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Toast.makeText(SecretManager.this, "복원 버튼 눌림", 0).show();
				}
			});
			
			return convertView;
		}
		
	}
}
