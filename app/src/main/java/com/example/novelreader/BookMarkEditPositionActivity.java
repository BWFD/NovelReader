package com.example.novelreader;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class BookMarkEditPositionActivity extends AppCompatActivity {
    private LinearLayout bookmarkList;
    Uri uri = Uri.parse("content://com.example.novelreader.bookmark/data");
    Cursor cursor;

    private Button upper;
    private Button next;
    private Button toTop;
    private Button toBottom;

    private List<Long> indexList = new ArrayList<>();

    private long currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_mark_edit_position);

        bookmarkList = findViewById(R.id.bookmarkList);
        currentIndex = -1;
        update();


        upper = findViewById(R.id.upper);
        next = findViewById(R.id.next);
        toTop = findViewById(R.id.toTop);
        toBottom = findViewById(R.id.toBottom);

        upper.setOnClickListener(view -> {
            change(currentIndex,currentIndex - 1);
        });
        next.setOnClickListener(view ->{
            change(currentIndex,currentIndex + 1);
        });
        toTop.setOnClickListener(view ->{
            change(currentIndex, 0);
        });
        toBottom.setOnClickListener(view ->{
            change(currentIndex, indexList.size()-1);
        });
    }

    void change(long index1, long index2) {
        if(index1 < 0 || index1 >= indexList.size()) return;
        if(index2 < 0 || index2 >= indexList.size()) return;

        long id1 = indexList.get(Math.toIntExact(index1));
        long id2 = indexList.get(Math.toIntExact(index2));

        ContentValues values1 = getContentValues(id1);
        ContentValues values2 = getContentValues(id2);
        if(values1 == null || values2 == null) {
            return;
        }
        String[] selectionArgs = new String[]{String.valueOf(id1)};
        getContentResolver().update(uri, values2, "_id = ?",selectionArgs);
        getContentResolver().notifyChange(uri, null);

        selectionArgs = new String[]{String.valueOf(id2)};
        getContentResolver().update(uri, values1, "_id = ?",selectionArgs);
        getContentResolver().notifyChange(uri, null);

        currentIndex = index2;
        update();
    }

    ContentValues getContentValues(long id) {
        String[] projection = {
                "bookName",
                "webSite",
                "chapterName",
                "chapterUrl",
                "TOTALHTML",
                "Scrolled"
        };
        String selection = "_id = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        cursor = getContentResolver().query(uri, projection, selection, selectionArgs, "_id DESC");
        ContentValues values = new ContentValues();

        if (cursor != null && cursor.moveToFirst()) {
            values.put("bookName",cursor.getString(cursor.getColumnIndexOrThrow("bookName")));
            values.put("webSite",cursor.getString(cursor.getColumnIndexOrThrow("webSite")));
            values.put("chapterName",cursor.getString(cursor.getColumnIndexOrThrow("chapterName")));
            values.put("chapterUrl",cursor.getString(cursor.getColumnIndexOrThrow("chapterUrl")));
            values.put("TOTALHTML",cursor.getString(cursor.getColumnIndexOrThrow("TOTALHTML")));
            values.put("Scrolled",cursor.getString(cursor.getColumnIndexOrThrow("Scrolled")));

            return values;
        }
        else {
            return null;
        }
    }

    void update() {
        bookmarkList.removeAllViews();
        cursor = getContentResolver().query(uri, null, null, null, "_id DESC");

        if (cursor != null) {
            int temp = 0;
            while (cursor.moveToNext()) {
                String text = "";
                String webSite = "";
                Button button = new Button(this);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                button.setLayoutParams(params);
                button.setGravity(Gravity.CENTER_VERTICAL);
                button.setPadding(10, 0, 0, 0);
                button.setTextColor(Color.WHITE);
                button.setBackgroundColor(Color.TRANSPARENT);

                int index = cursor.getColumnIndex("bookName");
                if (index != -1) {
                    text = cursor.getString(index);
                }
                index = cursor.getColumnIndex("webSite");
                if (index != -1) {
                    webSite = cursor.getString(index);
                    if (Objects.equals(webSite, "Piaotian")) {
                        text = text + "-" + "飄天文學網";
                    } else if (Objects.equals(webSite, "CZBooks")) {
                        text = text + "-" + "小說狂人";
                    } else if (Objects.equals(webSite, "hjwzw")) {
                        text = text + "-" + "黃金屋中文網";
                    } else {
                        text = text + "-" + webSite;
                    }
                }
                index = cursor.getColumnIndex("chapterName");
                if (index != -1) {
                    text = text + "\n" + cursor.getString(index);
                }
                index = cursor.getColumnIndex("_id");
                long id = 0;
                if (index != -1) {
                    id = cursor.getInt(index);
                    if(currentIndex == -1 && id == getIntent().getLongExtra("id",-1)) {
                        currentIndex = temp;
                    }
                    indexList.add(id);
                    temp ++;
                    if(currentIndex != -1 && id == indexList.get((int) currentIndex)) {
                        button.setTextColor(Color.GREEN);
                    }
                }
                button.setText(text);
                int finalTemp = temp;
                button.setOnClickListener(view ->{
                    currentIndex = finalTemp -1;
                    update();
                });


                bookmarkList.addView(button);

                View bar = new View(this);
                int color = ContextCompat.getColor(this, R.color.light_blue_600);
                bar.setBackgroundColor(color);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        6
                );
                bar.setLayoutParams(params);

                bookmarkList.addView(bar);
            }
        }
        cursor.close();
        bookmarkList.invalidate();
    }
}