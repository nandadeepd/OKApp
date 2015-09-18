package com.bigfatj.okpro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bigfatj.okpro.service.LocationService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import me.alexrs.prefs.lib.Prefs;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;
    CallbackManager cbm;
    String username, userID, prefFile = "prefs", regid, p_no = "78744295279";
    AccessToken at;
    LoginButton login;
    SharedPreferences prefs;
    GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_login);
        init();
        checkUserLogin();
        login.registerCallback(cbm, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                at = AccessToken.getCurrentAccessToken();
                userID = at.getUserId();
                username = Profile.getCurrentProfile().getFirstName();
                Prefs.with(LoginActivity.this).save("userID", userID.toString());
                storePrefs();
                getRegId();
                startService(new Intent(LoginActivity.this, LocationService.class));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });

    }

    private void checkUserLogin() {

        if (prefs.contains("user_id") && prefs.contains("username")) {
            goToMainActivity();
        }

    }

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(p_no);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                goToMainActivity();
                return msg;

            }

            @Override
            protected void onPostExecute(String msg) {
                //etRegId.setText(msg + "\n");
                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                new LoginTask().execute();
            }
        }.execute(null, null, null);
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {
        BufferedReader in;
        String result;

        @Override
        protected String doInBackground(Void... params) {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http");
                builder.authority("okapp.16mb.com");
                builder.appendPath("login.php").appendQueryParameter("name", username).appendQueryParameter("session_tok", regid)
                        .appendQueryParameter("fid", userID);
                HttpGet get = new HttpGet(builder.build().toString());
                HttpResponse response = httpclient.execute(get);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }
                in.close();
                result = sb.toString().split("<")[0].trim();
                Log.v("My Response :: ", result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String reslt) {
            if (result != null) {
                if (result.equals("success")) {
                    goToMainActivity();
                }
            }
        }
    }

    private void goToMainActivity() {
        Intent i = new Intent(this, OkAppActivity.class);
        startActivity(i);
    }


    private void storePrefs() {
        SharedPreferences.Editor e = prefs.edit();
        e.putString("user_id", userID);
        e.putString("username", username);
        e.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cbm.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        login = (LoginButton) findViewById(R.id.login_button);
        cbm = CallbackManager.Factory.create();
        setSupportActionBar(toolbar);
        prefs = getApplicationContext().getSharedPreferences(prefFile, MODE_PRIVATE);
        ViewCompat.setElevation(login, 8);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if (prefs.contains(userID) && prefs.contains(username)) {
            Intent i = new Intent(this, OkAppActivity.class);
            startActivity(i);
        }
        super.onResume();
    }
}
