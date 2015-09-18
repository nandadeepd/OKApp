package com.bigfatj.okpro.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aditya on 06-05-2015.
 */
public class FriendList implements Parcelable {
    String name;
    String id;


    // Parcelling part
    public FriendList(Parcel in) {
        String[] data = new String[2];

        in.readStringArray(data);
        this.id = data[0];
        this.name = data[1];
    }

    public FriendList() {

    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.id,
                this.name});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public FriendList createFromParcel(Parcel in) {
            return new FriendList(in);
        }

        public FriendList[] newArray(int size) {
            return new FriendList[size];
        }
    };
}
