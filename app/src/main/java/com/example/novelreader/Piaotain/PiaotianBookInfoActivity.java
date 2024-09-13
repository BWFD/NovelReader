package com.example.novelreader.Piaotain;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.novelreader.ChapterListAdapter;
import com.example.novelreader.R;
import com.example.novelreader.dao.PiaotianBookDetail;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.core.content.ContextCompat;

public class PiaotianBookInfoActivity extends AppCompatActivity {

    Activity activity = this;
    private ImageView imageView;
    private PiaotianBookDetail bookInfo;
    private TextView bookName;
    private TextView bookAuthor;
    private TextView bookDesc;
    private ListView chapterList;
    private ChapterListAdapter chapterListAdapter;
    private TextView loading;

    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_piaotian_book_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.bookInfoImage);
        String url = getIntent().getStringExtra("URL");
        bookName = findViewById(R.id.bookName);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookDesc = findViewById(R.id.bookDesc);
        chapterList = findViewById(R.id.chapterList);
        bookInfo = new PiaotianBookDetail();
        loading = findViewById(R.id.Loading);


        getBookInfo(url);

    }

    public void getBookInfo(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bookInfo = Piaotian.getBookDetail(url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.ic_launcher_round));
                        loadImage(bookInfo.getImageURL(),imageView);
                        bookName.setText(bookInfo.getName());
                        bookAuthor.setText(bookInfo.getAuthor());
                        bookDesc.setText(bookInfo.getDesc());

                        chapterListAdapter = new ChapterListAdapter(activity,bookInfo.getChapterName(),bookInfo.getChapterHTML(), bookInfo.getName());
                        chapterList.setAdapter(chapterListAdapter);

                        chapterListAdapter.notifyDataSetChanged();
                        chapterList.invalidateViews();
                        loading.setVisibility(View.INVISIBLE);

                    }
                });
            }
        }).start();
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 下載圖片
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(input);

                    // 在主線程更新 UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(chapterListAdapter != null) {
            chapterListAdapter.notifyDataSetChanged();
            //chapterList.invalidateViews();
        }
    }
}