package com.bigfatj.okpro.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bigfatj.okpro.R;
import com.bigfatj.okpro.utils.FriendList;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Aditya on 19-04-2015.
 */
public class FriendAdapter extends BaseAdapter {
    LayoutInflater v;
    ArrayList<FriendList> list;
    TextView uName, uID;
    Context con;
    String url = "https://graph.facebook.com/";
    RoundedImageView dPic;
    CheckBox cb;
    boolean flag;

    public FriendAdapter(Context con, ArrayList<FriendList> list, boolean show) {
        this.con = con;
        this.list = list;
        this.flag = show;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FriendList getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(con).inflate(R.layout.custom_list_item, null);
            uName = (TextView) convertView.findViewById(R.id.f_name);
            dPic = (RoundedImageView) convertView.findViewById(R.id.image_item);
            cb = (CheckBox) convertView.findViewById(R.id.checkbox);
            if (flag) {
                cb.setVisibility(View.INVISIBLE);
            }
        }

        Picasso.with(con).load(url + list.get(position).getId() + "/picture?width=9999").into(dPic);
        uName.setText(list.get(position).getName());
        return convertView;
    }
}
