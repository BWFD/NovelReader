package com.example.novelreader.Piaotain;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.novelreader.R;
import com.example.novelreader.dao.PiaotianClassification;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PiaotianMonthRankedFragment extends Fragment {
    View view;
    private ListView listView;
    private List<PiaotianClassification> dataList;
    private boolean notLoading;
    private int page;
    PiaotianBookListAdapter adapter;
    TextView loading;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 為此 Fragment 加載佈局
        view = inflater.inflate(R.layout.fragment_piaotian_month_ranked, container, false);
        // 在這裡初始化 TableLayout 或其他佈局內容
        page = 1;
        notLoading = true;
        dataList = new ArrayList<>();

        listView = view.findViewById(R.id.monthRankedList);
        loading = view.findViewById(R.id.MonthRankedLoading);

        adapter = new PiaotianBookListAdapter(getActivity(),dataList);

        listView.setAdapter(adapter);

        notLoading = false;
        getMonthRankedList();

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
                        getMonthRankedList();
                    }
                }
            }
        });

        return view;
    }

    public void getMonthRankedList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<PiaotianClassification> temp = Piaotian.getMonthRank(page);
                    if(temp != null) {
                        dataList.addAll(temp);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateUI();
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void updateUI() {
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
        listView.invalidateViews();
        page = page + 1;
        notLoading = true;
    }
}