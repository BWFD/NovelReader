package com.example.novelreader.Piaotain;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.novelreader.R;
import com.example.novelreader.dao.PiaotianClassification;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PiaotianClassificationListActivity extends AppCompatActivity {

    private ListView listView;
    private int page;
    private boolean notLoading;
    private List<PiaotianClassification> dataList;
    PiaotianBookListAdapter adapter;
    private TextView title;
    private float thumbPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_piaotian_classification_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.ClassificationInnerList);
        page = 1;
        notLoading = true;
        dataList = new ArrayList<>();
        adapter = new PiaotianBookListAdapter(this,dataList);
        title = findViewById(R.id.ClassificationInnerListTitle);

        title.setText(getIntent().getStringExtra("title"));

        listView.setAdapter(adapter);

        notLoading = false;
        updateUI();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                thumbPos = (float) (firstVisibleItem + visibleItemCount / 2) / totalItemCount;
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    // 到达底部，加载更多数据
                    if(notLoading) {
                        notLoading = false;
                        updateUI();
                    }
                }
            }
        });
    }

    public void updateUI() {
        new Thread(() -> {
            try {
                String url = getIntent().getStringExtra("URL");
                List<PiaotianClassification> temp = Piaotian.getBookListByUrl(url,page);
                if(temp != null) {
                    dataList.addAll(temp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            listView.setSelection(listView.getScrollY());
                            listView.invalidateViews();
                            page = page + 1;
                            notLoading = true;
                        }
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}