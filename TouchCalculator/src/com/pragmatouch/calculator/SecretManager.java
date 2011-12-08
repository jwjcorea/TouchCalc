package com.pragmatouch.calculator;

import java.util.ArrayList;

import android.R.bool;
import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class SecretManager extends Activity implements SensorListener {
	ListView userList;
	ArrayList<MyItem> m_arryItem;
	MyListAdapter MyAdapter;
	AddUserDialog dlg;
	UserInfo[] userInfo;
	int m_nPosLongClick;
	SensorManager sensorMgr;
	long lastUpdate;
	float x,y,z,last_x,last_y,last_z;
	
	public static int SHAKE_THRESHOLD = 500;
	
	
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("jdebug", "onResume");
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);

		MenuItem item = menu.add(0, 1, 0, "사용자 추가").setIcon(R.drawable.ic_input_add);		
		//item.setAlphabeticShortcut('a');
		menu.add(0, 2, 0, "비밀번호 변경").setIcon(R.drawable.ic_lock_lock);
		menu.add(0, 3, 0, "세팅").setIcon(R.drawable.ic_dialog_alert_holo_light);
		return true;
	}

	
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Intent i = new Intent();

		switch (item.getItemId()) {
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
			Log.i("jdebug", "setting");
			i.setClass(this, SettingActivity.class);
			startActivity(i);
			return true;
		}

		return false;
	}

	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secretmanager);
		
		RefreshList();
		
		userList.setOnItemClickListener(mItemClickListener);
		userList.setOnItemLongClickListener(mItemLongClickListener);
		registerForContextMenu(userList);
		
		// register the sensor manager
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorMgr.registerListener(this, SensorManager.SENSOR_ACCELEROMETER, SensorManager.SENSOR_DELAY_GAME);
		
		// screen fix
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.userList) {
			AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			
			menu.setHeaderTitle("사용자 설정");			
			String[] menuItems = getResources().getStringArray(R.array.menu);			
			for (int i = 0; i < menuItems.length; ++i)
			{
				if(i < 2)
					menu.add(Menu.NONE, i, i, menuItems[i]);
				else if(i ==2)
				{
					MyItem item = (MyItem)m_arryItem.get(m_nPosLongClick);
					boolean bMute = false;				
					if(item.bMute == 0)
						bMute = false;
					else
						bMute = true;
					
					menu.add(Menu.NONE, i, i, menuItems[i]).setCheckable(true).setChecked(bMute);
				}
				else if(i == 3)
				{
					MyItem item = (MyItem)m_arryItem.get(m_nPosLongClick);
					boolean bNoReceive = false;	
					if(item.bNoReceive == 0)
						bNoReceive = false;
					else
						bNoReceive = true;
					
					menu.add(Menu.NONE, i, i, menuItems[i]).setCheckable(true).setChecked(bNoReceive);
				}									
			}
		}
	}

	
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		
		switch(item.getItemId())
		{
		case 0:
			ModifyUserInfo();
			return true;
			
		case 1:
			{
				MyItem mListItem = (MyItem)m_arryItem.get(m_nPosLongClick);
				SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
				SQLiteDatabase db = dbHelper.getWritableDatabase();
						
				ContentValues cv = new ContentValues();
				cv.put("mute", item.isChecked());
				String strWhere = "name=\"" + mListItem.strName + "\" and tel=\"" + mListItem.strTel + "\"";
				db.delete("userList", strWhere, null);			
				
				// close the database
				db.close();
				
				m_arryItem.remove(m_nPosLongClick);
				MyAdapter.notifyDataSetChanged();
			}
			return true;
			
		case 2:
			{
				if(item.isChecked())item.setChecked(false);
				else item.setChecked(true);
				
				MyItem mListItem = (MyItem)m_arryItem.get(m_nPosLongClick);
				if(mListItem.strName.equals("") == false)
				{
					SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
					SQLiteDatabase db = dbHelper.getWritableDatabase();
							
					ContentValues cv = new ContentValues();
					cv.put("mute", item.isChecked());
					String strWhere = "name=\"" + mListItem.strName + "\" and tel=\"" + mListItem.strTel + "\"";
					db.update("userList", cv, strWhere, null);
					
					db.close();
				}
				
				//RefreshList();
				if(item.isChecked() == false)
					m_arryItem.get(m_nPosLongClick).bMute = 0;
				else
					m_arryItem.get(m_nPosLongClick).bMute = 1;
					
				MyAdapter.notifyDataSetChanged();
			}
			return true;
			
		case 3:
			{
				if(item.isChecked())item.setChecked(false);
				else item.setChecked(true);
				
				MyItem mListItem = (MyItem)m_arryItem.get(m_nPosLongClick);
				if(mListItem.strName.equals("") == false)
				{
					SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
					SQLiteDatabase db = dbHelper.getWritableDatabase();
							
					ContentValues cv = new ContentValues();
					cv.put("noreceive", item.isChecked());
					String strWhere = "name=\"" + mListItem.strName + "\" and tel=\"" + mListItem.strTel + "\"";
					db.update("userList", cv, strWhere, null);
					
					db.close();
				}
				
				//RefreshList();
				if(item.isChecked() == false)
					m_arryItem.get(m_nPosLongClick).bNoReceive = 0;
				else
					m_arryItem.get(m_nPosLongClick).bNoReceive = 1;
					
				MyAdapter.notifyDataSetChanged();
			}
			return true;
			
		default:
			super.onContextItemSelected(item);
			break;
			
		}
/*		int menuItemIdx = item.getItemId();
		
		if(menuItemIdx == 2)
		{
			
		}
				
		if (menuItemIdx == 0)
			ModifyUserInfo();*/

		return true;
	}

	void AddUser() {
		dlg = new AddUserDialog(this);
		dlg.show();		
	}
	
	void DropTable()
	{
		SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		// drop the table
		String sql = "DROP TABLE IF EXISTS USERLIST";
		db.execSQL(sql);
		dbHelper.onCreate(db);
		
		db.close();
	}
	
	void RefreshList()
	{
		m_arryItem = new ArrayList<MyItem>();		
		SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		
		String sql = "select * from userList;";		
		String[] columns = {"name", "tel", "mute", "noreceive", "restore"};
		Cursor result = db.query("userList", columns, null, null, null, null, null);
		
		String str = "";
		int i=0;
		while(result.moveToNext())
		{
			String name = result.getString(0);
			String tel = result.getString(1);
			int bMute = result.getInt(2);
			int bNoReceive = result.getInt(3);
			int bRestore = result.getInt(4);
			
			MyItem item = new MyItem(name, tel, bMute, bNoReceive, bRestore);
			m_arryItem.add(item);
			++i;
		}
				
		db.close();
		
		TextView tvEmpty = (TextView) findViewById(R.id.textEmpty);
		userList = (ListView) findViewById(R.id.userList);
		if(i == 0)
		{			
			tvEmpty.setVisibility(View.VISIBLE);
			userList.setVisibility(View.GONE);
		}
		else
		{	
			MyAdapter = new MyListAdapter(SecretManager.this, R.layout.listitem, m_arryItem);
			tvEmpty.setVisibility(View.GONE);
			userList.setVisibility(View.VISIBLE);
			userList.setAdapter(MyAdapter);
		}
	}

	public class AddUserDialog extends Dialog {
		//UserInfo[] userInfo = null;

		public AddUserDialog(Context context) {
			super(context);

			setTitle("사용자 추가");
			setContentView(R.layout.dialog_add_user);

			// TODO Auto-generated constructor stub
			Button btnTel = (Button) findViewById(R.id.btnTel);
			btnTel.setOnClickListener(new View.OnClickListener() {

				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					{
						// this popup dialog has to be hid
						hide();
						
						UserListDialog dlg = new UserListDialog(SecretManager.this);
						dlg.show();
						dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
							
							
							public void onDismiss(DialogInterface dialog) {
								// TODO Auto-generated method stub
								UserListDialog tmpDlg = ((UserListDialog) dialog);
								
								// get the value
							/*	String strName = tmpDlg.m_strName;
								String strTel = tmpDlg.m_strTel;
								
								// Insert info to DB
								SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
								SQLiteDatabase db = dbHelper.getWritableDatabase();
											
								ContentValues cv = new ContentValues();
								cv.put("name", strName);
								cv.put("tel", strTel);
								cv.put("mute", 0);
								cv.put("noreceive", 0);
								cv.put("restore", 0);
								db.insert("userList", null, cv);
								
								// close the database
								db.close();*/
								
								// refresh the list
								RefreshList();
							}
						});
					}
					/*					
					Cursor cursor = getTelList();

					int nCnt = cursor.getCount();
					if (nCnt == 0) {
						hide();
						return;
					} else {
						Log.d("jdebug", "end = " + nCnt);

						int i = 0;
						userInfo = new UserInfo[nCnt];
						for (i = 0; i < nCnt; ++i) {
							userInfo[i] = new UserInfo();
							userInfo[i].number = new ArrayList<String>();
						}

						if (cursor.moveToFirst()) {
							int idIndex = cursor.getColumnIndex("_id");

							i = 0;
							do {
								int id = cursor.getInt(idIndex);
								userInfo[i].strName = cursor.getString(1);

								String phoneChk = cursor.getString(2);
								if (phoneChk.equals("1")) {
									Cursor phones = getContentResolver()
											.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
													null,
													ContactsContract.CommonDataKinds.Phone.CONTACT_ID
															+ " = " + id, null,
													null);

									while (phones.moveToNext()) {
										userInfo[i].number.add(phones.getString(phones
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
									}
								}

								++i;

							} while (cursor.moveToNext() || i > nCnt);
						}

						// 사용자 이름과 폰 번호를 다 얻었다. 채워라
						for (i = 0; i < nCnt; ++i) {
							int nCntNum = userInfo[i].number.size();
							for (int j = 0; j < nCntNum; ++j) {
								Log.i("jdebug",
										"name = " + userInfo[i].strName
												+ " , number = "
												+ userInfo[i].number.get(j));
							}
						}

						// SecretManager.userInfo = userInfo;
						dismiss();
					}*/
				}
			});

			Button btnDirect = (Button) findViewById(R.id.btnDirect);
			btnDirect.setOnClickListener(new View.OnClickListener() {

				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					hide();

					ManuallyAddUserDialog dlg = new ManuallyAddUserDialog(SecretManager.this);
					dlg.show();
					dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
						
						
						public void onDismiss(DialogInterface dialog) {
							// TODO Auto-generated method stub
							ManuallyAddUserDialog tmpDlg = ((ManuallyAddUserDialog) dialog);
							
							if(tmpDlg.m_strName != null && tmpDlg.m_strTel != null)
							{	
								// get the value
								String strName = tmpDlg.m_strName;
								String strTel = tmpDlg.m_strTel;
								
								// Insert info to DB
								SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
								SQLiteDatabase db = dbHelper.getWritableDatabase();
											
								ContentValues cv = new ContentValues();
								cv.put("name", strName);
								cv.put("tel", strTel);
								cv.put("mute", 0);
								cv.put("noreceive", 0);
								cv.put("restore", 0);
								db.insert("userList", null, cv);
								
								// close the database
								db.close();
								
								// refresh the list
								RefreshList();
							}
						}
					});
				}
			});
		}
	}

	void ModifyUserInfo() {
		// get the info from the array
		MyItem selItem = (MyItem)m_arryItem.get(m_nPosLongClick);
		
		// show the dialog
		ModifyUserDialog dlg = new ModifyUserDialog(this);		
		dlg.SetInfo(selItem.strName, selItem.strTel);
		dlg.show();
		
		dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				RefreshList();
			}
		});
	}

	public class ManuallyAddUserDialog extends Dialog {
		String m_strName;
		String m_strTel;
		EditText m_editName;
		EditText m_editTel;		
		
		public ManuallyAddUserDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub

			setTitle("사용자 추가");
			setContentView(R.layout.dialog_direct_add_user);

			Button btnSave = (Button) findViewById(R.id.btnSave);
			Button btnCancel = (Button) findViewById(R.id.btnCancel);
			m_editName = (EditText) findViewById(R.id.editName);
			m_editTel = (EditText) findViewById(R.id.editTel);

			btnSave.setOnClickListener(new View.OnClickListener() {

				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					m_strName = m_editName.getText().toString();
					m_strTel = m_editTel.getText().toString();
					dismiss();
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					dismiss();
				}
			});
		}
	}
	
	public class UserListDialog extends Dialog {
		String m_strName;
		String m_strTel;
		UserInfo[] m_UserInfo = null;
		ListView m_listContact = null;
		ArrayList<MyItem> m_popupContactItem;
		MyPopupListAdapter m_myPopupListAdapter;
		
		public UserListDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub

			setTitle("사용자 추가");
			setContentView(R.layout.dialog_contact_list);
			
			Button btnSave = (Button) findViewById(R.id.btnSave);
			Button btnCancel = (Button) findViewById(R.id.btnCancel);	
			m_listContact = (ListView) findViewById(R.id.contactList);		
			
			// read the contact list
			ReadContactList();
						
			btnSave.setOnClickListener(new View.OnClickListener() {

				
				public void onClick(View arg0) {
					// TODO Auto-generated method stub					
					// check the checkbox has checked					
					
					// open the database
					SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					
					int nSize = m_popupContactItem.size();
					for(int i=0; i<nSize ; ++i)
					{
						if(m_myPopupListAdapter.arryCheckBox[i] == true)
						{							
							ContentValues cv = new ContentValues();
							cv.put("name", m_popupContactItem.get(i).strName);
							cv.put("tel", m_popupContactItem.get(i).strTel);
							cv.put("mute", 0);
							cv.put("noreceive", 0);
							cv.put("restore", 0);
							db.insert("userList", null, cv);							
						}
					}				
					
					// close the database
					db.close();
					
					// close the dialog
					dismiss();
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				
				public void onClick(View v) {
					// TODO Auto-generated method stub					
					dismiss();
				}
			});
		}
		
		void ReadContactList()
		{								
			Cursor cursor = getTelList();

			int nCnt = cursor.getCount();
			if (nCnt == 0) {
				hide();
				return;
			} else {
				Log.d("jdebug", "end = " + nCnt);

				int i = 0;
				m_UserInfo = new UserInfo[nCnt];
				for (i = 0; i < nCnt; ++i) {
					m_UserInfo[i] = new UserInfo();
					m_UserInfo[i].number = new ArrayList<String>();
				}

				if (cursor.moveToFirst()) {
					int idIndex = cursor.getColumnIndex("_id");

					i = 0;
					do {
						int id = cursor.getInt(idIndex);
						m_UserInfo[i].strName = cursor.getString(1);

						String phoneChk = cursor.getString(2);
						if (phoneChk.equals("1")) {
							Cursor phones = getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = " + id, null,
											null);

							while (phones.moveToNext()) {
								m_UserInfo[i].number.add(phones.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
							}
						}

						++i;

					} while (cursor.moveToNext() || i > nCnt);
				}

				m_popupContactItem = new ArrayList<MyItem>();
				
				// 사용자 이름과 폰 번호를 다 얻었다. 채워라
				for (i = 0; i < nCnt; ++i) {
					int nCntNum = m_UserInfo[i].number.size();
					for (int j = 0; j < nCntNum; ++j) {
						
						MyItem item = new MyItem(m_UserInfo[i].strName, m_UserInfo[i].number.get(j), 0, 0, 0);
						m_popupContactItem.add(item);
					}
				}
				
				dismiss();
			}
			
			m_myPopupListAdapter = new MyPopupListAdapter(SecretManager.this, R.layout.listitem2, m_popupContactItem);
			m_listContact.setAdapter(m_myPopupListAdapter);
			m_listContact.setOnItemClickListener(m_listContactListener);
		}
		
		AdapterView.OnItemClickListener m_listContactListener = new AdapterView.OnItemClickListener() {

			
			public void onItemClick(AdapterView parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String mes;
				mes = "Select Item = " + position;
				Log.i("jdebug", mes);
				Toast.makeText(SecretManager.this, mes, Toast.LENGTH_SHORT).show();				
			}
		};
		
		/*
		private void clickHandler(View view)
		{
			if(view.getId() == R.id.checkboxItem)
			{
				Log.i("jdebug", "hello jang");
			}
		}*/
	}	

	public class ModifyUserDialog extends Dialog {
		String m_strName;
		String m_strTel;
		
		void SetInfo(String strName, String strTel)
		{
			m_strName = strName;
			m_strTel = strTel;
			
			EditText editName = (EditText) findViewById(R.id.editName);
			EditText editTel = (EditText) findViewById(R.id.editTel);
			
			editName.setText(m_strName);
			editTel.setText(m_strTel);
		}
		
		public ModifyUserDialog(Context context) {
			super(context);
			// TODO Auto-generated constructor stub

			setTitle("사용자 추가");
			setContentView(R.layout.dialog_direct_add_user);

			Button btnSave = (Button) findViewById(R.id.btnSave);
			Button btnCancel = (Button) findViewById(R.id.btnCancel);						

			btnSave.setOnClickListener(new View.OnClickListener() {

				
				public void onClick(View arg0) {
					EditText editName = (EditText) findViewById(R.id.editName);
					EditText editTel = (EditText) findViewById(R.id.editTel);
					
					// open the database
					SQLiteOpenHelper dbHelper = new DBManager(SecretManager.this, "userList.db", null, 1);		
					SQLiteDatabase db = dbHelper.getWritableDatabase();
							
					ContentValues cv = new ContentValues();
					cv.put("name", editName.getText().toString());
					cv.put("tel", editTel.getText().toString());					
					String strWhere = "name=\"" + m_strName + "\" and tel=\"" + m_strTel + "\"";
					db.update("userList", cv, strWhere, null);
					
					// close the database
					db.close();
					
					// close the dialog
					dismiss();
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {

				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dismiss();
				}
			});
		}
	}

	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		
		public void onItemClick(AdapterView parnet, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			
			MyItem item = (MyItem)m_arryItem.get(position);			
			
			Intent i = new Intent(SecretManager.this, DetailUserActivity.class);
			i.putExtra("name", item.strName);
			i.putExtra("tel", item.strTel);
			startActivity(i);
		}
	};
	
	AdapterView.OnItemLongClickListener mItemLongClickListener = new AdapterView.OnItemLongClickListener() {

		
		public boolean onItemLongClick(AdapterView parnet, View view, int position, long id) {
			// TODO Auto-generated method stub
			m_nPosLongClick = position;
			
			return false;
		}
		
	};

	class MyItem {
		MyItem(String _strName, String _strTel, int _bMute, int _bNoReceive,
				int _bRestore) {
			strName = _strName;
			strTel = _strTel;
			bMute = _bMute;
			bNoReceive = _bNoReceive;
			bRestore = _bRestore;
		}

		String strName;
		String strTel;
		int bMute;
		int bNoReceive;
		int bRestore;
	}

	// the adapter class
	class MyListAdapter extends BaseAdapter {

		Context mainCon;
		LayoutInflater Inflater;
		ArrayList<MyItem> arrySrc;
		int layout;

		public MyListAdapter(Context context, int _layout,
				ArrayList<MyItem> _arrySrc) {
			mainCon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arrySrc = _arrySrc;
			layout = _layout;
		}

		
		public int getCount() {
			return arrySrc.size();
		}

		
		public Object getItem(int position) {
			return arrySrc.get(position).strName;
		}

		
		public long getItemId(int position) {
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final int pos = position;
			if (convertView == null)
				convertView = Inflater.inflate(layout, parent, false);

			// text
			TextView txtName = (TextView) convertView
					.findViewById(R.id.textName);
			TextView txtTel = (TextView) convertView.findViewById(R.id.textTel);

			txtName.setText(arrySrc.get(position).strName);
			txtTel.setText(arrySrc.get(position).strTel);
			
			if(arrySrc.get(position).bMute == 0)
			{
				ImageView imgV_mute = (ImageView) convertView.findViewById(R.id.imgV_mute);
				imgV_mute.setImageResource(R.drawable.ic_lock_silent_mode_off);
			}
			else
			{
				ImageView imgV_mute = (ImageView) convertView.findViewById(R.id.imgV_mute);
				imgV_mute.setImageResource(R.drawable.ic_lock_silent_mode);
			}
			
			if(arrySrc.get(position).bNoReceive == 1)
			{
				ImageView imgV_noReceive = (ImageView) convertView.findViewById(R.id.imgV_noReceiveCall);
				imgV_noReceive.setImageResource(R.drawable.sym_call_missed);
			}
			else
			{
				ImageView imgV_noReceive = (ImageView) convertView.findViewById(R.id.imgV_noReceiveCall);
				imgV_noReceive.setImageResource(R.drawable.sym_call_incoming);
			}

			return convertView;
		}
	}
	
	// the adapter class
	class MyPopupListAdapter extends BaseAdapter {

		Context mainCon;
		LayoutInflater Inflater;
		ArrayList<MyItem> arrySrc;
		boolean[] arryCheckBox = null;
		int layout;
		boolean checked;
		int m_nPos;

		public MyPopupListAdapter(Context context, int _layout,
				ArrayList<MyItem> _arrySrc) {
			mainCon = context;
			Inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			arrySrc = _arrySrc;
			layout = _layout;
			
			arryCheckBox = new boolean[getCount()];			
		}
		

		
		public int getCount() {
			return arrySrc.size();
		}

		
		public Object getItem(int position) {
			return arrySrc.get(position).strName;
		}

		
		public long getItemId(int position) {
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {			
			// TODO Auto-generated method stub
			final ViewHolder holder;			
			final int pos = position;
			
			if (convertView == null)
			{
				convertView = Inflater.inflate(layout, parent, false);
				holder = new ViewHolder();
				holder.txtName = (TextView) convertView.findViewById(R.id.textName);
				holder.txtTel = (TextView) convertView.findViewById(R.id.textTel);
				holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkboxItem);	
				convertView.setTag(holder);
			}
			else
				holder = (ViewHolder)convertView.getTag();

			holder.txtName.setText(arrySrc.get(position).strName);
			holder.txtTel.setText(arrySrc.get(position).strTel);
			
			// If the check box has checked, you have to set the value to the arryCheckBox
			//CheckBox chkBox = (CheckBox) convertView.findViewById(R.id.checkboxItem);
			//if(chkBox.isChecked())
				//arryCheckBox[position] = 1;
			//else
				//arryCheckBox[position] = 0;
			
			holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					arryCheckBox[pos] = isChecked;					
				}
			});
			
			holder.checkBox.setChecked(arryCheckBox[pos]);
		
			return convertView;
		}
		
		protected class ViewHolder{
			protected TextView txtName;
			protected TextView txtTel;
			protected CheckBox checkBox;
		}
	}

	private Cursor getTelList() {
		// Tel address
		Uri people = Contacts.CONTENT_URI;

		// 검색할 컬럼 정하기
		String[] projection = new String[] { Contacts._ID,
				Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER };

		// 쿼리 날려서 커서 얻기
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
				+ " COLLATE LOCALIZED ASC";

		return managedQuery(people, projection, null, selectionArgs, sortOrder);
	}

	class UserInfo {
		String strName;
		ArrayList<String> number;

		public UserInfo() {
			// TODO Auto-generated constructor stub
		}
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
