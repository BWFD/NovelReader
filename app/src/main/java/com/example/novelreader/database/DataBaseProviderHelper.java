package com.example.novelreader.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseProviderHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookmark.db";
    private static final int DATABASE_VERSION = 9;

    public DataBaseProviderHelper(Context context) {
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

        sqLiteDatabase.execSQL(
                "CREATE TABLE setting (" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "textSize TEXT," +
                        "textColor TEXT);"
        );

        sqLiteDatabase.execSQL(
                "INSERT INTO setting(textSize, textColor)" +
                "VALUES(\"15\", \"#AAAAAA\");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS bookmark");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS setting");
        onCreate(sqLiteDatabase);
    }
}
