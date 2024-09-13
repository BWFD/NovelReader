package com.example.novelreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookMarkProviderHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookmark.db";
    private static final int DATABASE_VERSION = 11;

    public BookMarkProviderHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE bookmark (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "bookName TEXT," +
                        "webSite TEXT," +
                        "chapterName TEXT," +
                        "chapterUrl TEXT," +
                        "TOTALHTML TEXT);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS bookmark");
        onCreate(sqLiteDatabase);
    }
}
