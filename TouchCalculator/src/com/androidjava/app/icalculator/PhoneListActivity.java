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
	        // 주소록 URI        
	        Uri people = Contacts.CONTENT_URI;
	        
	        // 검색할 컬럼 정하기
	        String[] projection = new String[] { Contacts._ID, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER };
	        
	        // 쿼리 날려서 커서 얻기
	        String[] selectionArgs = null;
	        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";    

	        // managedquery 는 activity 메소드이므로 아래와 같이 처리함
	        return getContentResolver().query(people, projection, null, selectionArgs, sortOrder);
	        // return managedQuery(people, projection, null, selectionArgs, sortOrder);
	    }

	    public void phoneBook() {
	        try {
	            Cursor cursor = getURI();                    // 전화번호부 가져오기    

	            int end = cursor.getCount();                // 전화번호부의 갯수 세기
	            //Log.i("ANDROES", "end = "+end);

	            String [] name = new String[end];    // 전화번호부의 이름을 저장할 배열 선언
	            String [] phone = new String[end];    // 전화번호부의 이름을 저장할 배열 선언
	            int count = 0;    

	            if(cursor.moveToFirst()) 
	            {
	                // 컬럼명으로 컬럼 인덱스 찾기 
	                int idIndex = cursor.getColumnIndex("_id");

	                do 
	                {
	                    // 요소값 얻기
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

	                    // 개별 연락처 삭제                    
	                    // rowNum = getBaseContext().getContentResolver().delete(RawContacts.CONTENT_URI, RawContacts._ID+ " =" + id,null);

	                    // LogCat에 로그 남기기
	                    // Logger.i("ANDROES", "id=" + id +", name["+count+"]=" + name[count]+", phone["+count+"]=" + phone[count]);
	                    count++;
	                    
	                } while(cursor.moveToNext() || count > end);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    
	    


}
