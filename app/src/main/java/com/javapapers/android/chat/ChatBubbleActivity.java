package com.javapapers.android.chat;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatBubbleActivity extends Activity implements  LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ChatActivity";

    private AQuery aq;
    LoaderManager loadermanager;

    private ChatSCA adapter;
    private ListView lvMessages;
    private EditText etMessageText;
    private Button btnMessageSend;

    private int testValue = 1;

    final int CL_MESSAGES_LIST = 100;
    final int CL_FIRST_USER = 1;
    final int CL_SECOND_USER = 2;

    final String LOG_TAG = "myLogs";

    final SimpleDateFormat dateFormatFromServer = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    Intent intent;
    private boolean side = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();

        loadermanager=getLoaderManager();

        setContentView(R.layout.activity_chat);

        aq = new AQuery(this);

        String from[] = { "from_user", "to_user" , "content" };
        int to[] = { R.id.from_user, R.id.to_user, R.id.content };
        adapter = new ChatSCA(this, R.layout.chat_list_element, null, from, to, 0);

        lvMessages = (ListView) findViewById(R.id.lvMessages);
        lvMessages.setAdapter(adapter);

//        buttonSend = (Button) findViewById(R.id.buttonSend);
//        listView = (ListView) findViewById(R.id.listView1);
//
//        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.activity_chat_singlemessage);
//        listView.setAdapter(chatArrayAdapter);
//
        etMessageText = (EditText) findViewById(R.id.etMessageText);
        btnMessageSend = (Button) findViewById(R.id.btnMessageSend);

//        chatText.setOnKeyListener(new OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
//                    return sendChatMessage();
//                }
//                return false;
//            }
//        });
//        buttonSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                sendChatMessage();
//            }
//        });

//        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//        listView.setAdapter(chatArrayAdapter);
//
//        //to scroll the list view to bottom on data change
//        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                listView.setSelection(chatArrayAdapter.getCount() - 1);
//            }
//        });

        loadermanager.initLoader(CL_FIRST_USER, null, this);
        loadermanager.initLoader(CL_SECOND_USER, null, this);
        loadermanager.initLoader(CL_MESSAGES_LIST, null, this);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("vmeste-gcm-message"));

        Timer myTimer = new Timer(); // Создаем таймер
        final Handler uiHandler = new Handler();
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateMessagesList(getLastMessageDate());
                    }
                });
            };
        }, 0L, 60L * 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateMessagesList(getLastMessageDate());
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

// Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("content");
            Log.d("receiver", "Got message: " + message);

            updateMessagesList(getLastMessageDate());
        }
    };

    public void onClickInsert(View v) {

//        final long lastMessageDate = getLastMessageDate(); // сначала берем дату последнего отправления а потом отправляем свое сообщение

        sendMessage("someMessage");
//        updateMessagesList(lastMessageDate);   ответы могут придти в другом порядке и отобразится 2 записи вместо одной
    }

    public void onClickUpdate(View v) {

        updateMessagesList(getLastMessageDate());

    }

    public void onClickDelete(View v) {
        int cnt = getContentResolver().delete(MyContactsProvider.MESSAGE_URI, null, null);
        Log.d(LOG_TAG, "delete, count = " + cnt);
    }

    public void onClickConfig(View v)
    {
        Intent intent = new Intent(this, ConfigActivity.class);
        startActivity(intent);
    }

    public void onClickMessageSend(View v)
    {
//        final long lastMessageDate = getLastMessageDate(); // сначала берем дату последнего отправления а потом отправляем свое сообщение

        updateMessagesList(getLastMessageDate());

        sendMessage(etMessageText.getText().toString());
        etMessageText.setText("");

//        updateMessagesList(lastMessageDate);   ответы могут придти в другом порядке и отобразится 2 записи вместо одной
    }


    public boolean flag = true;

    public void sendMessage(String message)
    {
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("from_user", UsersData.getInstance().getUserUuid(1));
        params.put("to_user", UsersData.getInstance().getUserUuid(2));

        params.put("content", message);

        aq.ajax("http://oroktestapp.appspot.com/json_message_send", params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if(json != null) {
                    jsonMessagesToDB(json);
                }
            }
        });
    }

    public void updateMessagesList(long fromDate)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("from_user", UsersData.getInstance().getUserUuid(1));
        params.put("to_user", UsersData.getInstance().getUserUuid(2));
        params.put("last_message_date", fromDate);

        aq.ajax("http://oroktestapp.appspot.com/json_message_list", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                jsonMessagesToDB(json);
            }
        });
    }

    public long getLastMessageDate() {
        Cursor cursor = getContentResolver().query(MyContactsProvider.MESSAGE_LASTDATE_URI, null, null, null, null);
        if (cursor.moveToFirst())
        {
            long res = cursor.getLong(0);
            cursor.close();
            return res;
        }
        return 0;
    }

    // Inset to DB data from array "messages" of json object
    private boolean jsonMessagesToDB (JSONObject json){

        if(json != null) {

            //json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).getJSONObject("image").getString("url")

            try {
                JSONArray ja = json.getJSONArray("messages");

                for (int i = 0; i< ja.length(); i++) {
                    jsonMessageObjectToDB((JSONObject) ja.get(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        return false;
    }

    // Inset to DB data from json Message object
    private boolean jsonMessageObjectToDB (JSONObject jo){

        try {
            ContentValues cv = new ContentValues();
            cv.put(MyContactsProvider.MESSAGE_FROM_USER, jo.optString("from_user", "Unknown").toString());
            cv.put(MyContactsProvider.MESSAGE_TO_USER, jo.optString("to_user", "Unknown").toString());
            cv.put(MyContactsProvider.MESSAGE_CONTENT, jo.optString("content", "Unknown").toString());
            cv.put(MyContactsProvider.MESSAGE_DATE, jo.optLong("date", 0));
            cv.put(MyContactsProvider.MESSAGE_KEY_ID, jo.optLong("key_id", 0));

//            Date date = dateFormatFromServer.parse(jo.optString("date", "Unknown").toString());
//            cv.put(MyContactsProvider.MESSAGE_DATE, date.getTime() / 1000);

            String selection = MyContactsProvider.MESSAGE_KEY_ID + " = ?";
            String[] selectionArgs = new String[] {String.valueOf(jo.optLong("key_id", 0))};
            Cursor cur = getContentResolver().query(
                    MyContactsProvider.MESSAGE_URI,
                    new String[] {MyContactsProvider.MESSAGE_KEY_ID},
                    selection,
                    selectionArgs,
                    null);

            if (cur.getCount() > 0)
            {
                int res = getContentResolver().update(MyContactsProvider.MESSAGE_URI, cv, selection, selectionArgs);
                Log.d(LOG_TAG, "update, result count : " + res);
            }
            else
            {
                Uri newUri = getContentResolver().insert(MyContactsProvider.MESSAGE_URI, cv);
                Log.d(LOG_TAG, "insert, result Uri : " + newUri.toString());
            }

            cur.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        Uri clUri = null;

        switch (arg0){
            case CL_MESSAGES_LIST:
                clUri = MyContactsProvider.MESSAGE_URI;
                break;
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
            case CL_MESSAGES_LIST:
                if (adapter != null && cursor != null)
                    adapter.swapCursor(cursor); //swap the new cursor in.
                else
                    Log.v(TAG, "OnLoadFinished: mAdapter is null");
                break;
            case CL_FIRST_USER:
                cursor.moveToFirst();
                //UsersData.getInstance().updateDataFromCursor(cursor);
                break;
            case CL_SECOND_USER:
                cursor.moveToFirst();
                //UsersData.getInstance().updateDataFromCursor(cursor);
                break;
        }
    }


    public void onLoaderReset(Loader<Cursor> arg0) {
        if(adapter!=null)
            adapter.swapCursor(null);
        else
            Log.v(TAG,"OnLoadFinished: mAdapter is null");
    }



}