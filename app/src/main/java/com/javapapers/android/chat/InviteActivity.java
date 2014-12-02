package com.javapapers.android.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InviteActivity extends Activity {
    private static final String TAG = "InviteActivity";

    private String invitationUuid = "";

    private AQuery aq;
    private EditText etPairEmail;
    private LinearLayout laySendInvite;
    private LinearLayout layViewInvitation;
    private LinearLayout layWaitAnswer;
    private TextView tvInvitationText;
    private TextView tvWaitAnswerText;
    private ProgressBar pbInviteWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        aq = new AQuery(this);

        etPairEmail = (EditText) findViewById(R.id.etPairEmail);
        laySendInvite = (LinearLayout) findViewById(R.id.laySendInvite);
        layViewInvitation = (LinearLayout) findViewById(R.id.layViewInvitation);
        layWaitAnswer = (LinearLayout) findViewById(R.id.layWaitAnswer);

        tvInvitationText = (TextView) findViewById(R.id.tvInvitationText);
        tvWaitAnswerText = (TextView) findViewById(R.id.tvWaitAnswerText);
        pbInviteWait = (ProgressBar) findViewById(R.id.pbInviteWait);

//        laySendInvite.setVisibility(View.GONE);
//        layViewInvitation.setVisibility(View.GONE);
//        layWaitAnswer.setVisibility(View.GONE);
//
//        getInvitations();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("vmeste-gcm-invite"));
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("content");
            Log.d("receiver", "Got message: " + message);

            getInvitations();
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();

        laySendInvite.setVisibility(View.GONE);
        layViewInvitation.setVisibility(View.GONE);
        layWaitAnswer.setVisibility(View.GONE);

        getInvitations();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.invite, menu);
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

    private void inviteButtonClick(View v, String uuid, String action, String pairEmail, String pairUuid){
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("uuid", uuid);
        params.put("action", action);
        params.put("pair_email", pairEmail);
        params.put("pair_uuid", pairUuid);

        aq.ajax("http://oroktestapp.appspot.com/invite_user", params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    try {
                        if (json.optInt("success", 0) > 0)
                        {
                            getInvitations();
                        }
                        else
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(InviteActivity.this);
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

    public void onClickWaitAnswerCancel(View v) {
        inviteButtonClick(v, UsersData.getInstance().getUserUuid(1), "cancel", etPairEmail.getText().toString(), invitationUuid);
    }
    public void onClickInvitationConfirm(View v) {
        inviteButtonClick(v, UsersData.getInstance().getUserUuid(1), "confirm", etPairEmail.getText().toString(), invitationUuid);
    }
    public void onClickInvitationReject(View v) {
        inviteButtonClick(v, UsersData.getInstance().getUserUuid(1), "reject", etPairEmail.getText().toString(), invitationUuid);
    }

    public void onClickInviteCouple(View v){
        inviteButtonClick(v, UsersData.getInstance().getUserUuid(1), "invite", etPairEmail.getText().toString(), invitationUuid);
    }

    private void getInvitations(){
        laySendInvite.setVisibility(View.GONE);
        layViewInvitation.setVisibility(View.GONE);
        layWaitAnswer.setVisibility(View.GONE);
        pbInviteWait.setVisibility(View.VISIBLE);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uuid", UsersData.getInstance().getUserUuid(1));
        params.put("action", "view");

        aq.ajax("http://oroktestapp.appspot.com/invite_user", params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if(json != null) {
                    try
                    {
                        JSONObject invite = json.getJSONObject("invite");
                        JSONObject user = json.getJSONObject("user");

                        String inviteStatus = invite.optString("status", "").toString();
                        if (inviteStatus.equals("invited"))
                        {
                            setViewInvitationActive(user);
                        }
                        if (inviteStatus.equals("pair"))
                        {
                            try {
                                ContentValues cv = new ContentValues();

                                cv.put(MyContactsProvider.USER_NAME, user.optString("name", "Unknown").toString());
                                cv.put(MyContactsProvider.USER_UUID, user.optString("uuid", "Unknown").toString());
                                cv.put(MyContactsProvider.USER_EMAIL, user.optString("email", "Unknown").toString());
                                cv.put(MyContactsProvider.USER_PASSWORD, user.optString("password", "Unknown").toString());
                                cv.put(MyContactsProvider.USER_KEY_ID, user.optString("key_id", "Unknown").toString());

                                int cnt = getContentResolver().update(MyContactsProvider.USER_SECOND_URI, cv, null, null);
                                Log.d(TAG, "update, count = " + cnt);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            finish();
                        }
                        if (inviteStatus.equals("fail"))
                        {
                            //setSendInviteActive();
                            getInvitations();
                        }
                        if (inviteStatus.equals("waitforanswer"))
                        {
                            setWaitAnswerActive(user);
                        }
                    }
                    catch (JSONException e) {
                        setSendInviteActive();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setSendInviteActive(){
        laySendInvite.setVisibility(View.VISIBLE);
        layViewInvitation.setVisibility(View.GONE);
        layWaitAnswer.setVisibility(View.GONE);
        pbInviteWait.setVisibility(View.GONE);
    }

    private void setWaitAnswerActive(JSONObject user){
        laySendInvite.setVisibility(View.GONE);
        layViewInvitation.setVisibility(View.GONE);
        layWaitAnswer.setVisibility(View.VISIBLE);
        pbInviteWait.setVisibility(View.GONE);

        tvWaitAnswerText.setText("Ждём ответа от " + user.optString("name", "").toString() + "(" + user.optString("email", "Unknown").toString() + ")" + " на приглашение быть Вместе");

        invitationUuid = user.optString("uuid", "").toString();
    }

    private void setViewInvitationActive(JSONObject user){
            laySendInvite.setVisibility(View.GONE);
            layWaitAnswer.setVisibility(View.GONE);
            layViewInvitation.setVisibility(View.VISIBLE);
            pbInviteWait.setVisibility(View.GONE);
            tvInvitationText.setText("" + user.optString("name", "").toString() + "(" + user.optString("email", "Unknown").toString() + ")" + " приглашает вас быть Вместе");

            invitationUuid = user.optString("uuid", "").toString();
    }
}
