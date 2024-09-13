package com.example.novelreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ReaderActivity extends AppCompatActivity {
    Activity activity = this;
    private TextView title;
    private TextView textView;
    private ArrayList<String> TOTALHTML;
    private Button prevButton;
    private Button nextButton;
    private String[] book;
    String url;
    Uri uri = Uri.parse("content://com.example.novelreader/data");
    private boolean isFirst = true;
    int index;

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
        if(isFirst) {
            this.url = getIntent().getStringExtra("currentHtml");
            this.TOTALHTML = getIntent().getStringArrayListExtra("TOTALHTML");
            isFirst = false;
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // 當用戶按下返回按鈕時，顯示彈窗
                if(getIntent().getStringExtra("isInBookMark").equals("true")) {
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
        System.out.println(index);
        System.out.println(TOTALHTML.size());
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
                    book = Piaotian.getChapter(url);
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
                        }

                        ContentValues values = new ContentValues();
                        values.put("chapterName",book[0]);
                        values.put("chapterUrl",url);
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
                values.put("webSite","飄天文學網");
                values.put("chapterName",book[0]);
                values.put("chapterUrl",url);
                values.put("TOTALHTML", TextUtils.join(",", TOTALHTML));

                activity.getContentResolver().insert(uri,values);
                // 用戶選擇是，退出應用或關閉 Activity
                finish();  // 關閉當前 Activity
            }
        });
        builder.create().show();
    }
}