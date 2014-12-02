package com.javapapers.android.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ConfigActivity extends Activity implements  LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "FirstUserActivity";

    private AQuery aq;

    private EditText etFirstId;
    private EditText etFirstName;
    private EditText etFirstUuid;
    private EditText etFirstKeyId;

    private EditText etSecondId;
    private EditText etSecondName;
    private EditText etSecondUuid;
    private EditText etSecondKeyId;

    LoaderManager loadermanager;

    int tmpVal = 1;


    final int CL_FIRST_USER = 1;
    final int CL_SECOND_USER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        aq = new AQuery(this);

        etFirstId = (EditText) findViewById(R.id.etFirstId);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etFirstUuid = (EditText) findViewById(R.id.etFirstUuid);
        etFirstKeyId = (EditText) findViewById(R.id.etFirstKeyId);

        etSecondId = (EditText) findViewById(R.id.etSecondId);
        etSecondName = (EditText) findViewById(R.id.etSecondName);
        etSecondUuid = (EditText) findViewById(R.id.etSecondUuid);
        etSecondKeyId = (EditText) findViewById(R.id.etSecondKeyId);

        loadermanager=getLoaderManager();
        loadermanager.initLoader(CL_FIRST_USER, null, this);
        loadermanager.initLoader(CL_SECOND_USER, null, this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.first_user, menu);
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

    public void onClickDisconnectCouple(View v) {

        Map<String, Object> params = new HashMap<String, Object>();

        params.put("uuid",  UsersData.getInstance().getUserUuid(1));
        params.put("action", "disconnect");
        params.put("pair_email", "");
        params.put("pair_uuid", UsersData.getInstance().getUserUuid(2));

        aq.ajax("http://oroktestapp.appspot.com/invite_user", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    try {
                        if (json.optInt("success", 0) > 0) {

                            ContentValues cv = new ContentValues();
                            cv.put(MyContactsProvider.USER_NAME, "");
                            cv.put(MyContactsProvider.USER_UUID, "");
                            cv.put(MyContactsProvider.USER_KEY_ID, "");
                            cv.put(MyContactsProvider.USER_EMAIL, "");
                            cv.put(MyContactsProvider.USER_PASSWORD, "");

                            int cnt = getContentResolver().update(MyContactsProvider.USER_SECOND_URI, cv, null, null);
                            Log.d(TAG, "update, count = " + cnt);

                            Intent intent = new Intent(ConfigActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ConfigActivity.this);
                            builder.setTitle("Важное сообщение!")
                                    .setMessage("Покормите кота!")
                                    .setIcon(R.drawable.ic_launcher)
                                    .setCancelable(false)
                                    .setNegativeButton("ОК",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
    public void onClickAccountExit(View v) {

        ContentValues cv = new ContentValues();
        cv.put(MyContactsProvider.USER_NAME, "");
        cv.put(MyContactsProvider.USER_UUID, "");
        cv.put(MyContactsProvider.USER_KEY_ID, "");
        cv.put(MyContactsProvider.USER_EMAIL, "");
        cv.put(MyContactsProvider.USER_PASSWORD, "");

        int cnt = getContentResolver().update(MyContactsProvider.USER_FIRST_URI, cv, null, null);
        Log.d(TAG, "update, count = " + cnt);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onClickFirstChange(View v) {

//        Map<String, Object> params = new HashMap<String, Object>();

        //params.put(MyContactsProvider.USER_UUID, etFirstUuid.getText().toString());
//        params.put("email", "mail@email.ru");
//        params.put("password", "123456789");
//
//        aq.ajax("http://oroktestapp.appspot.com/get_user", params, JSONObject.class, new AjaxCallback<JSONObject>() {
//
//            @Override
//            public void callback(String url, JSONObject json, AjaxStatus status) {
//                if(json != null) {
//                    try {
//                        JSONObject user = json.getJSONObject("user");
//
//                        try {
//                            ContentValues cv = new ContentValues();
//                            cv.put(MyContactsProvider.USER_NAME, user.optString("name", "Unknown").toString());
//                            cv.put(MyContactsProvider.USER_UUID, user.optString("uuid", "Unknown").toString());
//                            cv.put(MyContactsProvider.USER_KEY_ID, user.optString("key_id", "Unknown").toString());
//
//                            int cnt = getContentResolver().update(MyContactsProvider.USER_FIRST_URI, cv, null, null);
//                            Log.d(TAG, "update, count = " + cnt);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        ContentValues cv = new ContentValues();
        cv.put(MyContactsProvider.USER_NAME, "");
        cv.put(MyContactsProvider.USER_UUID, "");
        cv.put(MyContactsProvider.USER_KEY_ID, "");
        cv.put(MyContactsProvider.USER_EMAIL, "");
        cv.put(MyContactsProvider.USER_PASSWORD, "");

        int cnt = getContentResolver().update(MyContactsProvider.USER_FIRST_URI, cv, null, null);
        Log.d(TAG, "update, count = " + cnt);
    }

    public void onClickSecondChange(View v) {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("email", etSecondId.getText().toString());
        params.put("password", etSecondName.getText().toString());

        aq.ajax("http://oroktestapp.appspot.com/get_user", params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if(json != null) {
                    try {
                        JSONObject user = json.getJSONObject("user");

                        try {
                            ContentValues cv = new ContentValues();
                            cv.put(MyContactsProvider.USER_NAME, user.optString("name", "Unknown").toString());
                            cv.put(MyContactsProvider.USER_UUID, user.optString("uuid", "Unknown").toString());
                            cv.put(MyContactsProvider.USER_KEY_ID, user.optString("key_id", "Unknown").toString());

                            int cnt = getContentResolver().update(MyContactsProvider.USER_SECOND_URI, cv, null, null);
                            Log.d(TAG, "update, count = " + cnt);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

//        ContentValues cv = new ContentValues();
//        cv.put(MyContactsProvider.USER_NAME, etSecondName.getText().toString());
//        cv.put(MyContactsProvider.USER_UUID, etSecondUuid.getText().toString());
//        cv.put(MyContactsProvider.USER_KEY_ID, etSecondKeyId.getText().toString());
//        int cnt = getContentResolver().update(MyContactsProvider.USER_SECOND_URI, cv, null, null);
//        Log.d(TAG, "update, count = " + cnt);
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri clUri = null;

        switch (arg0){
            case CL_FIRST_USER:
                clUri = MyContactsProvider.USER_FIRST_URI;
                break;
            case CL_SECOND_USER:
                clUri = MyContactsProvider.USER_SECOND_URI;
                break;
        }

        return new CursorLoader(this, clUri, null, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader,Cursor cursor) {
        switch (loader.getId()) {
            case CL_FIRST_USER:
                cursor.moveToFirst();
                etFirstId.setText(cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_ID)));
                etFirstName.setText(cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_NAME)));
                etFirstUuid.setText(cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_UUID)));
                etFirstKeyId.setText(cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_KEY_ID)));

                //UsersData.getInstance().updateDataFromCursor(cursor);
                break;
            case CL_SECOND_USER:
                cursor.moveToFirst();
                etSecondId.setText(cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_ID)));
                etSecondName.setText(cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_NAME)));
                etSecondUuid.setText(cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_UUID)));
                etSecondKeyId.setText(cursor.getString(cursor.getColumnIndex(MyContactsProvider.USER_KEY_ID)));

                //UsersData.getInstance().updateDataFromCursor(cursor);
                break;
        }
    }

    public void onLoaderReset(Loader<Cursor> arg0) {
        Log.v(TAG, "onLoaderReset");
    }
}
