package com.bigfatj.okpro.fragments;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.bigfatj.okpro.database.GroupContract;
import com.bigfatj.okpro.database.GroupProvider;
import com.bigfatj.okpro.R;
import com.bigfatj.okpro.adapters.PeopleGridAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import me.alexrs.prefs.lib.Prefs;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveGroupsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    PeopleGridAdapter bigAdapter;
    GridView grid;
    int uid;
    String myUId = Prefs.with(getActivity()).getString("userID", "");

    public ActiveGroupsFragment() {
        // Required empty public constructor
    }

    public static ActiveGroupsFragment newInstance(int position) {
        ActiveGroupsFragment f = new ActiveGroupsFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_active_groups, container, false);
        grid = (GridView) v.findViewById(R.id.selected_list_dps);
        getLoaderManager().initLoader(0, null, this);
        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                GroupProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        bigAdapter = new PeopleGridAdapter(getActivity(), data);
        grid.setAdapter(bigAdapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, final long id) {

                new AlertDialogWrapper.Builder(getActivity())
                        .setTitle("PING")
                        .setMessage("Request a callback!")
                        .setPositiveButton("OK?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                uid = ((Cursor) grid.getAdapter().getItem(position)).getColumnIndex(GroupContract.USER_ID);
                                sendMessage("Ok?", uid);

                            }
                        }).show();
            }
        });

    }

    private void sendMessage(String s, int uid) {
        final String msg = s;
        final String userid = String.valueOf(uid);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                if (msg.length() > 0) {
                    HttpPost post = new HttpPost("http://okapp.16mb.com/sendmsg.php?ffid=" + userid + "&msg=" + msg);
                    DefaultHttpClient hc = new DefaultHttpClient();
                    try {
                        HttpResponse res = hc.execute(post);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
