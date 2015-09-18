package com.bigfatj.okpro.service.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bigfatj.okpro.database.GroupContract;
import com.bigfatj.okpro.database.GroupProvider;
import com.bigfatj.okpro.OkAppActivity;
import com.bigfatj.okpro.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class GcmMessageHandler extends IntentService {

    String message;
    private Handler handler;
    String url = "https://graph.facebook.com/";

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        for (String key : intent.getExtras().keySet()) {
            Object value = intent.getExtras().get(key);
            Log.d("Test", String.format("%s %s (%s)", key,
                    value.toString(), value.getClass().getName()));
        }

        message = extras.getString("msg");
        showToast();
        String[] data = message.split("%");
        Cursor cursor = getContentResolver().query(GroupProvider.CONTENT_URI, new String[]{GroupContract.NAME}, GroupContract.USER_ID + "=" + data[1], null, null);

        String name = "";
        if (cursor.moveToFirst())
            name = cursor.getString(cursor.getColumnIndex(GroupContract.NAME));
        message = data[0] + "   " + name;
        cursor.close();
        //showToast();
        showNotification(data[1], name);
        Log.i("GCM", "Received : (" + messageType + ")  " + extras.getString("message"));
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(String id, String name) {

        final String uname = name;
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        imageLoader.loadImage(url + id + "/picture?width=9999", new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {


                Intent notificationIntent = new Intent(GcmMessageHandler.this, OkAppActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(GcmMessageHandler.this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                mBuilder.setContentTitle(uname);
                mBuilder.setContentText("You OK?");
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentIntent(contentIntent);
                mBuilder.setLargeIcon(loadedImage);
                Notification n = mBuilder.build();
                mNotificationManager.notify(0, n);


            }
        });
        Log.d("test", "lol");

    }

    public void showToast() {
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });

    }
}