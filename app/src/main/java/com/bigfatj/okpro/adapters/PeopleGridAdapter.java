package com.bigfatj.okpro.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bigfatj.okpro.database.GroupContract;

import com.bigfatj.okpro.R;

import com.squareup.picasso.Picasso;


/**
 * Created by Aditya on 23-05-2015.
 */

public class PeopleGridAdapter extends CursorAdapter {
    ImageView profile_pic;
    TextView name;
    View grid;
    String url = "https://graph.facebook.com/";

    public PeopleGridAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        grid = LayoutInflater.from(context).inflate(R.layout.custom_grid_item, null);
        profile_pic = (ImageView) grid.findViewById(R.id.grid_image_item);
        name = (TextView) grid.findViewById(R.id.name);
        return grid;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Picasso.with(context)
                .load(url + cursor.getString(cursor.getColumnIndex(GroupContract.USER_ID)) + "/picture?width=9999")
                .into(profile_pic);
        name.setText(cursor.getString(cursor.getColumnIndex(GroupContract.NAME)));
    }
}
