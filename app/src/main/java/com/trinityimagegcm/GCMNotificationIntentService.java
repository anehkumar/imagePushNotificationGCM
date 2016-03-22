package com.trinityimagegcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aneh on 2/23/2016.
 */
public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static String TAG = "GCMNotificationIntentService";
    Bitmap Images;
    String message;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);


        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            sendNotification((String) extras.get("msg"));
            }
        }
        GcmReceiver.completeWakefulIntent(intent);
    }


    private void sendNotification(String msg) {

        try {
            JSONObject jsonObject = new JSONObject(msg);
            Images = getBitmapFromURL(jsonObject.getString("image"));
            message = jsonObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent in = new Intent(this, MainActivity.class);
        in.putExtra("Notif", message);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                in, 0);

        int numMessages = 0;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                /*.setStyle(new NotificationCompat.BigTextStyle().bigText("dfds"))*/
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(Images))
                .setAutoCancel(true)
                .setContentText(message)
                .setNumber(++numMessages);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}