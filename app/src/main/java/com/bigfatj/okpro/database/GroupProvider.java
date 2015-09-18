package com.bigfatj.okpro.database;

/**
 * Created by Aditya on 26-06-2015.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.bigfatj.okpro.database.GroupContract;

import java.util.HashMap;

public class GroupProvider extends ContentProvider {
    // fields for my content provider
    static final String PROVIDER_NAME = "com.bigfatj.okpro.groups";
    static final String URL = "content://" + PROVIDER_NAME + "/users";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    // integer values used in content URI
    static final int USER = 1;
    static final int U_ID = 2;

    DBHelper dbHelper;

    // projection map for a query
    private static HashMap<String, String> GroupMap;

    // maps content URI "patterns" to the integer values that were set above
    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "users", USER);
        uriMatcher.addURI(PROVIDER_NAME, "users/*", U_ID);
    }

    // database declarations
    private SQLiteDatabase database;
    static final String DATABASE_NAME = "GROUP";
    static final String TABLE_NAME = "groups";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    GroupContract.USER_ID + " TEXT UNIQUE NOT NULL, " +
                    GroupContract.NAME + " TEXT NOT NULL, " +
                    GroupContract.LATITUDE + " TEXT NOT NULL, " +
                    GroupContract.LONGITUDE + " TEXT NOT NULL, " +
                    GroupContract.TIME + " TEXT NOT NULL); ";

    // class that creates and manages the provider's database
    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DBHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ". Old data will be destroyed");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();

        return database != null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            // maps all database column names
            case USER:
                queryBuilder.setProjectionMap(GroupMap);
                break;
            case U_ID:
                queryBuilder.appendWhere(GroupContract.USER_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == "") {
            sortOrder = GroupContract.USER_ID;
        }
        Cursor cursor = queryBuilder.query(database, projection, selection,
                selectionArgs, null, null, sortOrder);
        /**
         * register to watch a content URI for changes
         */
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long row = database.insert(TABLE_NAME, "", values);

        if (row > 0) {
            Uri newUri = ContentUris.withAppendedId(CONTENT_URI, row);
            getContext().getContentResolver().notifyChange(newUri, null);
            return newUri;
        }
        throw new SQLException("Fail to add a new record into " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case USER:
                count = database.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case U_ID:
                count = database.update(TABLE_NAME, values, GroupContract.USER_ID +
                        " = " + uri.getLastPathSegment() +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)) {
            case USER:
                // delete all the records of the table
                count = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case U_ID:
                String id = uri.getLastPathSegment();    //gets the id
                count = database.delete(TABLE_NAME, GroupContract.USER_ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;


    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            // Get all course records
            case USER:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "user";
            // Get a particular course
            case U_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "user";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


}