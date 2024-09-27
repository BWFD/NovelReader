package com.example.novelreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.novelreader.CZBooks.CZBooksBookInfoActivity;
import com.example.novelreader.Piaotain.PiaotianBookInfoActivity;
import com.example.novelreader.service.CZBooks;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.ArrayList;

public class ReaderActivity extends AppCompatActivity {
    Activity activity = this;
    private TextView title;
    private TextView textView;
    private ArrayList<String> TOTALHTML;
    private Button prevButton;
    private Button nextButton;
    private String[] book;
    private String webSite;
    String url;
    Uri uri = Uri.parse("content://com.example.novelreader.bookmark/data");
    Uri setting = Uri.parse("content://com.example.novelreader.setting/data");
    private boolean isFirst = true;
    int index;
    private Button settingButton;
    private ScrollView scrollView;

    String textSize="15";
    String textColor="#AAAAAA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reader);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        title = findViewById(R.id.bookTitle);
        textView = findViewById(R.id.chapterContext);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        settingButton = findViewById(R.id.settingButton);
        scrollView = findViewById(R.id.ReaderScrollView);

        this.webSite = getIntent().getStringExtra("webSite");
        if(isFirst) {
            this.url = getIntent().getStringExtra("currentHtml");
            this.TOTALHTML = getIntent().getStringArrayListExtra("TOTALHTML");
            isFirst = false;
        }

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(activity, view);
                popupMenu.getMenuInflater().inflate(R.menu.readersettingmenu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if(id == R.id.openBook) {
                            Intent intent;
                            if(webSite.equals("Piaotian")) {
                                intent = new Intent(activity, PiaotianBookInfoActivity.class);
                                intent.putExtra("URL","CHAPTER:" + url);
                                startActivity(intent);
                                finish();
                            }
                            else
                            if(webSite.equals("CZBooks")) {
                                intent = new Intent(activity, CZBooksBookInfoActivity.class);
                                intent.putExtra("URL","CHAPTER:" + url);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else
                        if(id == R.id.wordSetting) {
                            Intent intent = new Intent(activity, WordSettingActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        else {
                            return true;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 當用戶按下返回按鈕時，顯示彈窗
                if(getIntent().getStringExtra("isInBookMark").equals("true")) {
                    ContentValues values = new ContentValues();
                    values.put("chapterName",book[0]);
                    values.put("chapterUrl",url);
                    values.put("scrolled",scrollView.getScrollY());
                    String[] selectionArgs = new String[]{getIntent().getStringExtra("bookName")};

                    getContentResolver().update(uri, values, "bookName = ?",selectionArgs);
                    getContentResolver().notifyChange(uri, null);
                    finish();
                }
                else {
                    showExitConfirmationDialog();
                }
            }
        });


        index = 0;
        for(int i=0;i<TOTALHTML.size(); i++) {
            if(TOTALHTML.get(i).equals(url)) {
                index = i;
                break;
            }
        }
        if(index == 0) {
            prevButton.setBackgroundColor(Color.WHITE);

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity,ReaderActivity.class);
                    intent.putExtra("currentHtml", TOTALHTML.get(index + 1));
                    intent.putStringArrayListExtra("TOTALHTML",TOTALHTML);
                    intent.putExtra("isInBookMark",getIntent().getStringExtra("isInBookMark"));
                    intent.putExtra("bookName",getIntent().getStringExtra("bookName"));
                    intent.putExtra("webSite",webSite);
                    intent.putExtra("scrolled",0);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else
        if(index == TOTALHTML.size() -1 ){
            nextButton.setBackgroundColor(Color.WHITE);

            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity,ReaderActivity.class);
                    intent.putExtra("currentHtml", TOTALHTML.get(index - 1));
                    intent.putStringArrayListExtra("TOTALHTML",TOTALHTML);
                    intent.putExtra("isInBookMark",getIntent().getStringExtra("isInBookMark"));
                    intent.putExtra("bookName",getIntent().getStringExtra("bookName"));
                    intent.putExtra("webSite",webSite);
                    intent.putExtra("scrolled",0);
                    startActivity(intent);
                    finish();
                }
            });
        }
        else {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity,ReaderActivity.class);
                    intent.putExtra("currentHtml", TOTALHTML.get(index + 1));
                    intent.putStringArrayListExtra("TOTALHTML",TOTALHTML);
                    intent.putExtra("isInBookMark",getIntent().getStringExtra("isInBookMark"));
                    intent.putExtra("bookName",getIntent().getStringExtra("bookName"));
                    intent.putExtra("webSite",webSite);
                    intent.putExtra("scrolled",0);
                    startActivity(intent);
                    finish();
                }
            });

            prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(activity,ReaderActivity.class);
                    intent.putExtra("currentHtml", TOTALHTML.get(index - 1));
                    intent.putStringArrayListExtra("TOTALHTML",TOTALHTML);
                    intent.putExtra("isInBookMark",getIntent().getStringExtra("isInBookMark"));
                    intent.putExtra("bookName",getIntent().getStringExtra("bookName"));
                    intent.putExtra("webSite",webSite);
                    intent.putExtra("scrolled",0);
                    startActivity(intent);
                    finish();
                }
            });

        }
        book = new String[2];
        updateBook(url);
    }

    public void updateBook(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(webSite.equals("Piaotian")) {
                        book = Piaotian.getChapter(url);
                    }
                    else
                    if(webSite.equals("CZBooks")) {
                        book = CZBooks.getChapter(url);
                    }
                    else {
                        book = null;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(book[0]);
                        if(book[1].isEmpty()) {
                            textView.setText("來源網站排版有問題，建議到別的網站觀看!!!");
                            textView.setTextSize(40);
                            textView.setGravity(Gravity.CENTER_VERTICAL);
                        }
                        else {
                            textView.setText(book[1]);
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.smoothScrollTo(0, getIntent().getIntExtra("scrolled",0));  // 平滑滾動到指定位置
                                }
                            });

                        }

                        ContentValues values = new ContentValues();
                        values.put("chapterName",book[0]);
                        values.put("chapterUrl",url);
                        values.put("scrolled",scrollView.getScrollY());
                        String[] selectionArgs = new String[]{getIntent().getStringExtra("bookName")};

                        getContentResolver().update(uri, values, "bookName = ?",selectionArgs);
                        getContentResolver().notifyChange(uri, null);
                    }
                });
            }
        }).start();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加書籤");
        builder.setMessage("要加入書籤嗎?");

        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 用戶選擇否，取消對話框
                finish();
            }
        });

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ContentValues values = new ContentValues();
                values.put("bookName",getIntent().getStringExtra("bookName"));
                values.put("webSite",webSite);
                values.put("chapterName",book[0]);
                values.put("chapterUrl",url);
                values.put("TOTALHTML", TextUtils.join(",", TOTALHTML));
                values.put("scrolled",scrollView.getScrollY());

                activity.getContentResolver().insert(uri,values);
                // 用戶選擇是，退出應用或關閉 Activity
                finish();  // 關閉當前 Activity
            }
        });
        builder.create().show();
    }
    @Override
    protected void onResume() {
        super.onResume();

        Cursor cursor = getContentResolver().query(setting,null,null,null,null);
        if(cursor!=null) {
            while(cursor.moveToNext()) {
                int index = cursor.getColumnIndex("textSize");
                if(index!=-1) {
                    textSize = cursor.getString(index);
                }
                index = cursor.getColumnIndex("textColor");
                if(index!=-1) {
                    textColor = cursor.getString(index);
                }
            }
        }
        cursor.close();
        textView.setTextColor(Color.parseColor(textColor));
        textView.setTextSize(Integer.parseInt(textSize));
    }
}