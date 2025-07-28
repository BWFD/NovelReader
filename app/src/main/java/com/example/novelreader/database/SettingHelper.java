package com.example.novelreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "setting.db";
    private static final int DATABASE_VERSION = 6;

    public SettingHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE setting (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "only TEXT," +
                        "textSize TEXT," +
                        "textColor TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS setting");
        onCreate(sqLiteDatabase);
    }
}
