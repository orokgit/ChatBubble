package com.javapapers.android.chat;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        CursorLoader cl = new CursorLoader(this, MyContactsProvider.USER_FIRST_URI, null, null, null, null);
//        Cursor cursor = cl.loadInBackground();
//        cursor.moveToFirst();
//        String name = cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_NAME));
//        String uuid = cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_UUID));
//        int keyId = cursor.getInt(cursor.getColumnIndex(MyContactsProvider.USER_KEY_ID));
//
//        if(uuid.length() <= 0) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!UsersData.getInstance().isUserSet(1))
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        else
        {
            if (!UsersData.getInstance().isUserSet(2)){
                Intent intent = new Intent(this, InviteActivity.class);
                startActivity(intent);
            }
//            else
//            {
//                Intent intent = new Intent(this, ChatBubbleActivity.class);
//                startActivity(intent);
//            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickMainChat(View v)
    {
        Intent intent = new Intent(this, ChatBubbleActivity.class);
        startActivity(intent);
    }
    public void onClickMainConfig(View v)
    {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }
}
