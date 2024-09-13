package com.example.novelreader.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SettingProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.novelreader.setting";
    private static final String PATH = "data";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);


    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        SettingHelper providerHelper = new SettingHelper(getContext());
        database = providerHelper.getWritableDatabase();

        insertInitialData();

        return (database != null);
    }

    public void insertInitialData() {
        Cursor cursor = database.query("setting", null, null, null, null, null, null);
        if(cursor.getCount() == 0) {
            ContentValues values = new ContentValues();
            values.put("only", "only");
            values.put("textSize", "15");
            values.put("textColor", "#AAAAAA");


            database.insert("setting", null, values);
        }
        cursor.close();
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return database.query("setting",strings, s, strings1, null, null, s1);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowId = database.insert("setting", null, contentValues);
        if (rowId > 0) {
            return ContentUris.withAppendedId(CONTENT_URI, rowId);
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return database.delete("setting", s, strings);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return database.update("setting", contentValues, s, strings);
    }
}
