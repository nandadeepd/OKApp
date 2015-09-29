package com.bigfatj.okpro.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import me.alexrs.prefs.lib.Prefs;

public class NotifBarReplier extends BroadcastReceiver {
 
@Override
 public void onReceive(final Context context, Intent intent) {
    final String uid = intent.getStringExtra("user");
    new AsyncTask<Void, Void, String>() {
        @Override
        protected String doInBackground(Void... voids) {
                Log.d("userid", uid);
                HttpPost post = new HttpPost("http://okapp.16mb.com/sendmsg.php?ffid=" + Prefs.with(context).getString("userID","") + "&msg=imok&fid="+uid);
                DefaultHttpClient hc = new DefaultHttpClient();
                try {
                    HttpResponse res = hc.execute(post);
                    Log.d("check", EntityUtils.toString(res.getEntity()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return null;
        }
    }.execute();
 }
 
}
