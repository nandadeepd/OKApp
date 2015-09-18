package com.bigfatj.okpro.fragments;


import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigfatj.okpro.database.GroupContract;
import com.bigfatj.okpro.database.GroupProvider;
import com.bigfatj.okpro.R;
import com.bigfatj.okpro.utils.BaseLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.alexrs.prefs.lib.Prefs;


/**
 * A simple {@link Fragment} subclass.
 */
public class LocateFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    BaseLocation curr_loc;
    GoogleMap googleMap;
    List<String> userIDs = new ArrayList<>();
    List<String> usernames = new ArrayList<>();
    List<LatLng> locationVals = new ArrayList<>();
    double latitude, longitude;
    MarkerOptions options = new MarkerOptions();
    LatLng pos;
    String userID;
    String responseBody;
    MapView mapView;

    public static LocateFragment newInstance(int position) {
        LocateFragment f = new LocateFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    public LocateFragment() {
        // Required empty public constructor
    }

    public class GetLocationTable extends AsyncTask<Void, Void, Void> {
        String userID;

        public GetLocationTable(String userID) {

            this.userID = userID;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpClient hpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://okapp.16mb.com/getLocation.php?fid=" + userID);

            try {
                HttpResponse response = hpClient.execute(post);
                responseBody = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("response", responseBody);
            try {
                JSONObject obj = new JSONObject(responseBody);
                String lat = obj.get("lat").toString();
                String lon = obj.get("lon").toString();
                if (!lat.equals("null") && !lon.equals("null")) {
                    LatLng loc = new LatLng(Long.parseLong(lat), Long.parseLong(lon));
                    locationVals.add(loc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    public void setLocationMarker() {
        for (int i = 0; i < locationVals.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(locationVals.get(i));
            markerOptions.title(usernames.get(i));
            googleMap.addMarker(markerOptions);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_locate, container, false);
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        googleMap = mapView.getMap();
        googleMap.setMyLocationEnabled(true);
        curr_loc = new BaseLocation(getActivity());
        Prefs.with(getActivity()).getString("userID", userID);
        //updateLocation();
        latitude = curr_loc.getLatitude();
        longitude = curr_loc.getLongitude();
        MapsInitializer.initialize(getActivity());
        pos = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10);
        options.title("YOU");
        options.position(pos);
        googleMap.addMarker(options);
        googleMap.animateCamera(cameraUpdate);
        Cursor userIdCursor = getActivity().getContentResolver().query(GroupProvider.CONTENT_URI, new String[]{GroupContract.USER_ID, GroupContract.NAME}, null, null, null);
        if (userIdCursor.getCount() > 0) {
            while (userIdCursor.moveToNext()) {
                userIDs.add(userIdCursor.getString(userIdCursor.getColumnIndex(GroupContract.USER_ID)));
                usernames.add(userIdCursor.getString(userIdCursor.getColumnIndex(GroupContract.NAME)));
            }
        }
        userIdCursor.close();

        for (int i = 0; i < userIDs.size(); i++) {
            Log.d("user id log", userIDs.get(i));
            new GetLocationTable(userIDs.get(i)).execute();
        }
        setLocationMarker();
        return v;

    }

    private class updateLocation extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            HttpClient hpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://okapp.16mb.com/updatelocation.php?fid=" + userID + "&lat=" + curr_loc.getLatitude() + "&lon=" + curr_loc.getLongitude());

            try {
                HttpResponse response = hpClient.execute(post);
                responseBody = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.d("response", responseBody);
            try {
                JSONObject obj = new JSONObject(responseBody);
                String lat = obj.get("lat").toString();
                String lon = obj.get("lon").toString();
                if (!lat.equals("null") && !lon.equals("null")) {
                    LatLng loc = new LatLng(Long.parseLong(lat), Long.parseLong(lon));
                    locationVals.add(loc);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        curr_loc.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        curr_loc.unregister();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
