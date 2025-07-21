package com.example.novelreader.hjwzw;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.novelreader.R;
import com.example.novelreader.dao.hjwzwClassification;
import com.example.novelreader.service.hjwzw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class hjwzwBookListActivity extends AppCompatActivity {

    private ListView listView;
    private List<hjwzwClassification> dataList;
    private boolean notLoading;
    private int page;
    hjwzwBooksAdapter adapter;
    TextView loading;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hjwzw_book_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        page = 1;
        notLoading = true;
        dataList = new ArrayList<>();

        listView = findViewById(R.id.ClassificationInnerList);
        loading = findViewById(R.id.MonthRankedLoading);
        title = findViewById(R.id.ClassificationInnerListTitle);

        adapter = new hjwzwBooksAdapter(this,dataList);

        listView.setAdapter(adapter);

        notLoading = false;
        getBookList();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    // 到达底部，加载更多数据
                    if(notLoading) {
                        notLoading = false;
                        getBookList();
                    }
                }
            }
        });
    }

    public void getBookList() {
        String url = getIntent().getStringExtra("URL");
        String titleStr = getIntent().getStringExtra("title");
        title.setText(titleStr);
        new Thread(() -> {
            try {
                List<hjwzwClassification> temp = hjwzw.getBookList(url,page);
                if(temp != null) {
                    dataList.addAll(temp);
                    runOnUiThread(() -> updateUI());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void updateUI() {
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
        listView.setSelection(listView.getScrollY());
        listView.invalidateViews();
        page = page + 1;
        notLoading = true;
    }
}