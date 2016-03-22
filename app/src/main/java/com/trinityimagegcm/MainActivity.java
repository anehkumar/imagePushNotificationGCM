package com.trinityimagegcm;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements GetResponse {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static String TAG = "Mainactivity";
    public static final String REG_ID = "regId";
    String regId, msg;
    SharedPreferences prefs;
    GoogleCloudMessaging gcm;
    Context context = this;
    private AsyncReuse requestServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gcm = GoogleCloudMessaging.getInstance(this);
        prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        regId = getRegistrationId();
    }

    private String getRegistrationId() {
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    private void saveRegisterId(Context context, String regId) {
        SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        Log.i(TAG, "Saving regId on app version ");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.commit();
    }

    public void uploadToServer(View view) {
        JSONObject jObject = new JSONObject();
        executeServerReq();
        try {
            jObject.put("reg", regId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestServer.getObjectQ(jObject);
        requestServer.execute();
    }

    private void executeServerReq() {
        requestServer = new AsyncReuse("http://demo.trinitytuts.com/push/saveid.php", true, MainActivity.this);
        requestServer.getResponse = this;
    }

    public void registerInBackground(View view) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register("260680606263");
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toast.makeText(getApplicationContext(), "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();
                saveRegisterId(context, regId);
            }
        }.execute(null, null, null);
    }

    @Override
    public Void getData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data.toString());
            Log.e("here", "==========" + jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

