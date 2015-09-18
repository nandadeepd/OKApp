package com.bigfatj.okpro;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.bigfatj.okpro.adapters.TabPagerAdapter;
import com.bigfatj.okpro.utils.FriendList;
import com.bigfatj.okpro.service.LocationService;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.Profile;

import java.util.ArrayList;


public class OkAppActivity extends AppCompatActivity {

    Toolbar toolbar;
    String name, userID;
    Profile p;
    AccessToken at;
    ImageView prof;
    PagerSlidingTabStrip tabStrip;
    ViewPager viewPager;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, LocationService.class));
        init();
        tabs2config();
        checkForLocation();
    }

    private void checkForLocation() {

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), "GPS Enabled", Toast.LENGTH_SHORT).show();
        } else {
            buildGPSenablerDialog();
        }
    }

    private void buildGPSenablerDialog() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enable Location services").setCancelable(true);
        alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent i = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        });
        alertDialogBuilder.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getApplicationContext(), "For better results enable GPS", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void init() {
        at = AccessToken.getCurrentAccessToken();
        p = Profile.getCurrentProfile();
        name = p.getFirstName();
        userID = p.getId();
        prof = (ImageView) findViewById(R.id.image);
        toolbar = (Toolbar) findViewById(R.id.app_bar_mainAct);
        setSupportActionBar(toolbar);
    }

    private void tabs2config() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
        if (id == R.id.action_create) {
            listOfFriends();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void listOfFriends() {

        Intent i = new Intent(this, FriendsActivity.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            ArrayList<FriendList> list = data.getParcelableArrayListExtra("friends");
            //  pc.setData(list);
        }
    }
}
