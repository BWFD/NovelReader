package com.example.novelreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
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
import com.example.novelreader.hjwzw.hjwzwBookInfoActivity;
import com.example.novelreader.service.CZBooks;
import com.example.novelreader.service.NetworkUtil;
import com.example.novelreader.service.Piaotian;
import com.example.novelreader.service.hjwzw;

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
    Uri download = Uri.parse("content://com.example.novelreader.download/data");
    private boolean isFirst = true;
    int index;
    private Button settingButton;
    private ScrollView scrollView;
    private TextView loading;

    String textSize="15";
    String textColor="#AAAAAA";
    boolean disconnect = false;

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
        disconnect = false;

        if (!NetworkUtil.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, NetworkError.class);
            startActivity(intent);
            finish();
            disconnect = true;
            return;
        }

        title = findViewById(R.id.bookTitle);
        textView = findViewById(R.id.chapterContext);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        settingButton = findViewById(R.id.settingButton);
        scrollView = findViewById(R.id.ReaderScrollView);
        loading = findViewById(R.id.loading);

        this.webSite = getIntent().getStringExtra("webSite");
        if(isFirst) {
            this.url = getIntent().getStringExtra("currentHtml");
            this.TOTALHTML = getIntent().getStringArrayListExtra("TOTALHTML");
            isFirst = false;
        }

        settingButton.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(activity, view);
            popupMenu.getMenuInflater().inflate(R.menu.readersettingmenu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if(id == R.id.openBook) {
                    Intent intent;
                    switch (webSite) {
                        case "Piaotian":
                            intent = new Intent(activity, PiaotianBookInfoActivity.class);
                            intent.putExtra("URL", "CHAPTER:" + url);
                            startActivity(intent);
                            finish();
                            break;
                        case "CZBooks":
                            intent = new Intent(activity, CZBooksBookInfoActivity.class);
                            intent.putExtra("URL", "CHAPTER:" + url);
                            startActivity(intent);
                            finish();
                            break;
                        case "hjwzw":
                            intent = new Intent(activity, hjwzwBookInfoActivity.class);
                            intent.putExtra("URL", "CHAPTER:" + url);
                            startActivity(intent);
                            finish();
                            break;
                    }
                }
                else
                if(id == R.id.wordSetting) {
                    Intent intent = new Intent(activity, WordSettingActivity.class);
                    startActivity(intent);
                    return true;
                }
                // TODO 預載小說 根據不同的小說網call service下載
                if(id == R.id.download) {
                    switch (webSite) {
                        case "Piaotian":
                            new Thread(() -> {
                                try {
                                    Piaotian.download(this,TOTALHTML);
                                } catch (Exception e) {
                                    Intent intent = new Intent(this, ErrorPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).start();
                            break;
                        case "CZBooks":
                            new Thread(() -> {
                                try {
                                    CZBooks.download(this,TOTALHTML);
                                } catch (Exception e) {
                                    Intent intent = new Intent(this, ErrorPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).start();
                            break;
                        case "hjwzw":
                            new Thread(() -> {
                                try {
                                    hjwzw.download(this,TOTALHTML);
                                } catch (Exception e) {
                                    Intent intent = new Intent(this, ErrorPage.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).start();
                            break;
                    }
                }
                else {
                    return true;
                }
                return true;
            });
            popupMenu.show();
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
                    String[] selectionArgs = new String[]{String.valueOf(getIntent().getLongExtra("id",-1))};

                    getContentResolver().update(uri, values, "_id = ?",selectionArgs);
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

            nextButton.setOnClickListener(view -> {
                Intent intent = new Intent(activity,ReaderActivity.class);
                intent.putExtra("currentHtml", TOTALHTML.get(index + 1));
                intent.putStringArrayListExtra("TOTALHTML",TOTALHTML);
                intent.putExtra("isInBookMark",getIntent().getStringExtra("isInBookMark"));
                intent.putExtra("bookName",getIntent().getStringExtra("bookName"));
                intent.putExtra("webSite",webSite);
                intent.putExtra("scrolled",0);
                intent.putExtra("id",getIntent().getLongExtra("id",-1));
                startActivity(intent);
                finish();
            });
        }
        else
        if(index == TOTALHTML.size() -1 ){
            nextButton.setBackgroundColor(Color.WHITE);

            prevButton.setOnClickListener(view -> {
                Intent intent = new Intent(activity,ReaderActivity.class);
                intent.putExtra("currentHtml", TOTALHTML.get(index - 1));
                intent.putStringArrayListExtra("TOTALHTML",TOTALHTML);
                intent.putExtra("isInBookMark",getIntent().getStringExtra("isInBookMark"));
                intent.putExtra("bookName",getIntent().getStringExtra("bookName"));
                intent.putExtra("webSite",webSite);
                intent.putExtra("scrolled",0);
                intent.putExtra("id",getIntent().getLongExtra("id",-1));
                startActivity(intent);
                finish();
            });
        }
        else {
            nextButton.setOnClickListener(view -> {
                Intent intent = new Intent(activity,ReaderActivity.class);
                intent.putExtra("currentHtml", TOTALHTML.get(index + 1));
                intent.putStringArrayListExtra("TOTALHTML",TOTALHTML);
                intent.putExtra("isInBookMark",getIntent().getStringExtra("isInBookMark"));
                intent.putExtra("bookName",getIntent().getStringExtra("bookName"));
                intent.putExtra("webSite",webSite);
                intent.putExtra("scrolled",0);
                intent.putExtra("id",getIntent().getLongExtra("id",-1));
                startActivity(intent);
                finish();
            });

            prevButton.setOnClickListener(view -> {
                Intent intent = new Intent(activity,ReaderActivity.class);
                intent.putExtra("currentHtml", TOTALHTML.get(index - 1));
                intent.putStringArrayListExtra("TOTALHTML",TOTALHTML);
                intent.putExtra("isInBookMark",getIntent().getStringExtra("isInBookMark"));
                intent.putExtra("bookName",getIntent().getStringExtra("bookName"));
                intent.putExtra("webSite",webSite);
                intent.putExtra("scrolled",0);
                intent.putExtra("id",getIntent().getLongExtra("id",-1));
                startActivity(intent);
                finish();
            });

        }
        book = new String[2];
        String [] args = new String[]{url};
        Cursor cursor = getContentResolver().query(download,null,"chapterUrl = ?",args,null);
        if(cursor.getCount() > 0 ) {
            updateBookFromDatabase(cursor);
        }
        else {
        updateBookFormService(url);
        }
    }

    public void updateBookFromDatabase(Cursor cursor) {
        new Thread(() -> {
            runOnUiThread(() -> {
                if(cursor != null && cursor.moveToNext()) {
                    int index = cursor.getColumnIndex("chapterName");
                    if(index != -1) {
                        String name = cursor.getString(index);
                        book[0] = name;
                        title.setText(name);
                    }

                    index = cursor.getColumnIndex("novel");
                    if(index != -1) {
                        textView.setText(cursor.getString(index));
                    }

                    index = cursor.getColumnIndex("scrolled");
                    if(index != -1) {
                        int finalIndex = index;
                        scrollView.post(() -> {
                            scrollView.smoothScrollTo(0, finalIndex);  // 平滑滾動到指定位置
                        });
                    }
                }
                cursor.close();
                loading.setText("");
                loading.setVisibility(View.INVISIBLE);
            });
        }).start();
    }

    public void updateBookFormService(String url) {
        new Thread(() -> {
            try {
                if(webSite.equals("Piaotian")) {
                    book = Piaotian.getChapter(url);
                }
                else
                if(webSite.equals("CZBooks")) {
                    book = CZBooks.getChapter(url);
                }
                else
                if(webSite.equals("hjwzw")) {
                    book = hjwzw.getChapter(url);
                }
                else {
                    book = null;
                }
            } catch (IOException e) {
                Intent intent = new Intent(this, ErrorPage.class);
                startActivity(intent);
                finish();
            }

            runOnUiThread(() -> {
                title.setText(book[0]);
                if(book[1] != null && book[1].isEmpty()) {
                    textView.setText("來源網站排版有問題，建議到別的網站觀看!!!");
                    textView.setTextSize(40);
                    textView.setGravity(Gravity.CENTER_VERTICAL);
                }
                else {
                    textView.setText(book[1]);
                    loading.setText("");
                    loading.setVisibility(View.INVISIBLE);
                    scrollView.post(() -> {
                        scrollView.smoothScrollTo(0, getIntent().getIntExtra("scrolled",0));  // 平滑滾動到指定位置
                    });

                }

                ContentValues values = new ContentValues();
                values.put("chapterName",book[0]);
                values.put("chapterUrl",url);
                values.put("scrolled",scrollView.getScrollY());
                String[] selectionArgs = new String[]{String.valueOf(getIntent().getLongExtra("id",-1))};

                getContentResolver().update(uri, values, "_id = ?",selectionArgs);
                getContentResolver().notifyChange(uri, null);
            });
        }).start();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加書籤");
        builder.setMessage("要加入書籤嗎?");

        builder.setNegativeButton("否", (dialog, which) -> {
            // 用戶選擇否，取消對話框
            finish();
        });

        builder.setPositiveButton("是", (dialog, which) -> {

            ContentValues values = new ContentValues();
            values.put("bookName",getIntent().getStringExtra("bookName"));
            values.put("webSite",webSite);
            values.put("chapterName",book[0]);
            values.put("chapterUrl",url);
            values.put("TOTALHTML", TextUtils.join("|", TOTALHTML));
            values.put("scrolled",scrollView.getScrollY());

            activity.getContentResolver().insert(uri,values);
            // 用戶選擇是，退出應用或關閉 Activity
            finish();  // 關閉當前 Activity
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

    @Override
    protected void onUserLeaveHint() {
        if(disconnect) return;
        super.onUserLeaveHint();

        ContentValues values = new ContentValues();
        values.put("chapterName",book[0]);
        values.put("chapterUrl",url);
        values.put("scrolled",scrollView.getScrollY());
        String[] selectionArgs = new String[]{String.valueOf(getIntent().getLongExtra("id",-1))};


        getContentResolver().update(uri, values, "_id = ?",selectionArgs);
        getContentResolver().notifyChange(uri, null);
    }
}