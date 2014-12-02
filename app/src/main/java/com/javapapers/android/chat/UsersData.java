package com.javapapers.android.chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class UsersData {
    private static final String TAG = "UsersData";


    private static UsersData instance = new UsersData();
    public Context context;

    public final int firstUserId = 1;
    public String firstUserName = "first";
    public String firstUserUuid = "1";
    public int firstUserKeyId = 1;

    public final int secondUserId = 2;
    public String secondUserName = "second";
    public String secondUserUuid = "2";
    public int secondUserKeyId = 2;

    private UsersData(){
    }

    public static UsersData getInstance(){
        return  instance;
    }

    public void setContext(Context cxt){
        context = cxt;
    }

    public boolean isUserSet(int userId){
        Uri uri;

        switch (userId)
        {
            case MyContactsProvider.FIRST_USER_ID:
                uri = MyContactsProvider.USER_FIRST_URI;
                break;
            case MyContactsProvider.SECOND_USER_ID:
                uri = MyContactsProvider.USER_SECOND_URI;
                break;
            default:
                return false;
        }

        CursorLoader cl;
        cl = new CursorLoader(context, uri, null, null, null, null);
        Cursor cursor = cl.loadInBackground();
        cursor.moveToFirst();
        String uuid = cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_UUID));

        cursor.close();
        return uuid.length() > 0;
    }

    public boolean setGcmId(String gcmId){
        if (gcmId.length() > 0)
        {
            ContentValues cv = new ContentValues();
            cv.put(MyContactsProvider.USER_GCM_ID, gcmId);

            int cnt = context.getContentResolver().update(MyContactsProvider.USER_FIRST_URI, cv, null, null);
            Log.d(TAG, "update, count = " + cnt);

        }
        return false;
    }

    public String getGcmId(){
        CursorLoader cl;
        cl = new CursorLoader(context, MyContactsProvider.USER_FIRST_URI, null, null, null, null);
        Cursor cursor = cl.loadInBackground();
        cursor.moveToFirst();

        String res = cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_GCM_ID));

        cursor.close();
        return res;
    }

    public String getUserUuid(int userId){
        Uri uri;

        switch (userId)
        {
            case MyContactsProvider.FIRST_USER_ID:
                uri = MyContactsProvider.USER_FIRST_URI;
                break;
            case MyContactsProvider.SECOND_USER_ID:
                uri = MyContactsProvider.USER_SECOND_URI;
                break;
            default:
                return null;
        }

        CursorLoader cl;
        cl = new CursorLoader(context, uri, null, null, null, null);
        Cursor cursor = cl.loadInBackground();
        cursor.moveToFirst();

        String res = cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_UUID));

        cursor.close();
        return res;
    }

//    public void updateDataFromCursor(Cursor cursor){
//        if (cursor.moveToFirst())
//        {
//            String name = cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_NAME));
//            String uuid = cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_UUID));
//            int keyId = cursor.getInt(cursor.getColumnIndex(MyContactsProvider.USER_KEY_ID));
//
//
//            switch (cursor.getInt(cursor.getColumnIndex(MyContactsProvider.USER_ID)))
//            {
//                case firstUserId:
//                    if (name != firstUserName) firstUserName = name;
//                    if (uuid != firstUserUuid) firstUserUuid = uuid;
//                    if (keyId != firstUserKeyId) firstUserKeyId = keyId;
//                    break;
//                case secondUserId:
//                    if (name != secondUserName) secondUserName = name;
//                    if (uuid != secondUserUuid) secondUserUuid = uuid;
//                    if (keyId != secondUserKeyId) secondUserKeyId = keyId;
//                    break;
//            }
//        }
//    }



}
