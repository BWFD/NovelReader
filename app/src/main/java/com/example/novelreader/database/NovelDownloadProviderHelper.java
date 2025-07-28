package com.example.novelreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NovelDownloadProviderHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "download.db";
    private static final int DATABASE_VERSION = 1;

    public NovelDownloadProviderHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE download (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "chapterName TEXT, " +
                        "novel TEXT," +
                        "scrolled INTEGER," +
                        "website TEXT," +
                        "TOTALHTML TEXT," +
                        "chapterUrl TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS download");
        onCreate(sqLiteDatabase);
    }
}
