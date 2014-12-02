package com.javapapers.android.chat;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.mymodule.backend.registration.Registration;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by orok on 25.11.2014.
 */
class GcmRegistrationAsyncTask extends AsyncTask<Context, Void, String> {
    private static Registration regService = null;
    private GoogleCloudMessaging gcm;
    private Context context;

    // TODO: change to your own sender ID to Google Developers Console project number, as per instructions above
    private static final String SENDER_ID = "279994024003";

    @Override
    protected String doInBackground(Context... params) {
        if (regService == null) {
//            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
//                    new AndroidJsonFactory(), null)
//                    // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
//                    // otherwise they can be skipped
//                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
//                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                        @Override
//                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
//                                throws IOException {
//                            abstractGoogleClientRequest.setDisableGZipContent(true);
//                        }
//                    });
//            // end of optional local run code

            Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);

            regService = builder.build();
        }

        context = params[0];

        String msg = "";
        try {
            if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
            String regId = gcm.register(SENDER_ID);
            msg = "Device registered, registration ID=" + regId;

            // You should send the registration ID to your server over HTTP,
            // so it can use GCM/HTTP or CCS to send messages to your app.
            // The request to your server should be authenticated if your app
            // is using accounts.
            //regService.register(regId).execute();
            if (regId.length() > 0)
            {
                UsersData.getInstance().setGcmId(regId);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            msg = "Error: " + ex.getMessage();
        }
        return msg;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Logger.getLogger("REGISTRATION").log(Level.INFO, msg);
    }
}
