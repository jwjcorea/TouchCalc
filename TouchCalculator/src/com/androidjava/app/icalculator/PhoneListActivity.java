package com.androidjava.app.icalculator;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

public class PhoneListActivity extends Activity {

	
	   private Cursor getURI() 
	    {
	        // �ּҷ� URI        
	        Uri people = Contacts.CONTENT_URI;
	        
	        // �˻��� �÷� ���ϱ�
	        String[] projection = new String[] { Contacts._ID, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER };
	        
	        // ���� ������ Ŀ�� ���
	        String[] selectionArgs = null;
	        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";    

	        // managedquery �� activity �޼ҵ��̹Ƿ� �Ʒ��� ���� ó����
	        return getContentResolver().query(people, projection, null, selectionArgs, sortOrder);
	        // return managedQuery(people, projection, null, selectionArgs, sortOrder);
	    }

	    public void phoneBook() {
	        try {
	            Cursor cursor = getURI();                    // ��ȭ��ȣ�� ��������    

	            int end = cursor.getCount();                // ��ȭ��ȣ���� ���� ����
	            //Log.i("ANDROES", "end = "+end);

	            String [] name = new String[end];    // ��ȭ��ȣ���� �̸��� ������ �迭 ����
	            String [] phone = new String[end];    // ��ȭ��ȣ���� �̸��� ������ �迭 ����
	            int count = 0;    

	            if(cursor.moveToFirst()) 
	            {
	                // �÷������� �÷� �ε��� ã�� 
	                int idIndex = cursor.getColumnIndex("_id");

	                do 
	                {
	                    // ��Ұ� ���
	                    int id = cursor.getInt(idIndex);        
	                    String phoneChk = cursor.getString(2);
	                    if (phoneChk.equals("1")) {
	                        Cursor phones = getContentResolver().query(
	                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
	                                null,
	                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
	                                        + " = " + id, null, null);

	                        while (phones.moveToNext()) {
	                            phone[count] = phones
	                                    .getString(phones
	                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
	                        }        
	                    }
	                    name[count] = cursor.getString(1);
	                    
	                    //Log.i("===========================>",name[count]);

	                    // ���� ����ó ����                    
	                    // rowNum = getBaseContext().getContentResolver().delete(RawContacts.CONTENT_URI, RawContacts._ID+ " =" + id,null);

	                    // LogCat�� �α� �����
	                    // Logger.i("ANDROES", "id=" + id +", name["+count+"]=" + name[count]+", phone["+count+"]=" + phone[count]);
	                    count++;
	                    
	                } while(cursor.moveToNext() || count > end);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    


}
