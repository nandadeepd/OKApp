package com.bigfatj.okpro;

import android.content.ContentValues;
import android.support.v4.view.ViewCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.bigfatj.okpro.database.GroupContract;
import com.bigfatj.okpro.database.GroupProvider;
import com.bigfatj.okpro.utils.FriendList;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import com.bigfatj.okpro.adapters.FriendAdapter;

public class FriendsActivity extends AppCompatActivity {

    AccessToken at;
    String fbToken;
    GraphRequest request;
    String id, name;
    ArrayList<FriendList> friends_list = new ArrayList<>();
    ArrayList<FriendList> selected_list = new ArrayList<>();
    Toolbar toolbar;
    FriendAdapter adapter;
    ListView list;
    Button group;
    View v;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_friends);
        init();
        getUserFriends();
        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSendData();
            }
        });
        ViewCompat.setElevation(group, 8);
    }

    private void getAndSendData() {
        for (int i = 0; i < adapter.getCount(); i++) {
            v = list.getChildAt(i);
            checkBox = (CheckBox) v.findViewById(R.id.checkbox);
            if (checkBox.isChecked()) {
                selected_list.add(adapter.getItem(i));
                ContentValues cv = new ContentValues();
                cv.put(GroupContract.USER_ID, adapter.getItem(i).getId());
                cv.put(GroupContract.NAME, adapter.getItem(i).getName());
                cv.put(GroupContract.LATITUDE, 0.0);
                cv.put(GroupContract.LONGITUDE, 0.0);
                cv.put(GroupContract.TIME, 0123);
                getContentResolver().insert(GroupProvider.CONTENT_URI, cv);

            }
        }
        finish();
    }


    private void init() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_friends"));
        at = AccessToken.getCurrentAccessToken();
        fbToken = AccessToken.getCurrentAccessToken().toString();
        Toast.makeText(getApplicationContext(), fbToken, Toast.LENGTH_LONG).show();
        toolbar = (Toolbar) findViewById(R.id.app_bar_f);
        setSupportActionBar(toolbar);
        list = (ListView) findViewById(R.id.friends_list);
        group = (Button) findViewById(R.id.create);
    }

    private void getUserFriends() {

        request = GraphRequest.newMyFriendsRequest(at, new GraphRequest.GraphJSONArrayCallback() {
            @Override
            public void onCompleted(JSONArray list, GraphResponse graphResponse) {

                // Toast.makeText(getApplicationContext(),"response success",Toast.LENGTH_SHORT).show();
                graphResponse.getConnection();
                JSONObject jsonObject = graphResponse.getJSONObject();
                try {
                    list = jsonObject.getJSONArray("data");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject jso = list.getJSONObject(i);
                        id = jso.getString("id");
                        name = jso.getString("name");
                        FriendList f = new FriendList();
                        f.setId(id);
                        f.setName(name);
                        //  System.out.println(f.getName().toString());
                        friends_list.add(f);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setUserFriends();
            }
        });
        request.executeAsync();
    }

    private void setUserFriends() {
        adapter = new FriendAdapter(this, friends_list, false);
        list.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
