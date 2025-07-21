package com.example.novelreader.CZBooks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.novelreader.R;
import com.example.novelreader.dao.CZBooksClassification;
import com.example.novelreader.service.CZBooks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CZBooksBookListFragment extends Fragment {
    View view;
    private ListView listView;
    private List<CZBooksClassification> dataList;
    private boolean notLoading;
    private int page;
    CZBooksBookListAdapter adapter;
    TextView loading;

    public static CZBooksBookListFragment classificationNew(String classification) {
        CZBooksBookListFragment fragment = new CZBooksBookListFragment();
        Bundle args = new Bundle();
        args.putString("classification", classification);
        fragment.setArguments(args);
        return fragment;
    }

    public static CZBooksBookListFragment classificationDay(String classification) {
        CZBooksBookListFragment fragment = new CZBooksBookListFragment();
        Bundle args = new Bundle();
        args.putString("classification", classification + "/day");
        fragment.setArguments(args);
        return fragment;
    }

    public static CZBooksBookListFragment classificationWeek(String classification) {
        CZBooksBookListFragment fragment = new CZBooksBookListFragment();
        Bundle args = new Bundle();
        args.putString("classification", classification + "/week");
        fragment.setArguments(args);
        return fragment;
    }

    public static CZBooksBookListFragment classificationTotal(String classification) {
        CZBooksBookListFragment fragment = new CZBooksBookListFragment();
        Bundle args = new Bundle();
        args.putString("classification", classification + "/total");
        fragment.setArguments(args);
        return fragment;
    }

    public static CZBooksBookListFragment classificationFinish(String classification) {
        CZBooksBookListFragment fragment = new CZBooksBookListFragment();
        Bundle args = new Bundle();
        args.putString("classification", classification + "/finish");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_c_z_books_book_list, container, false);

        page = 1;
        notLoading = true;
        dataList = new ArrayList<>();

        listView = view.findViewById(R.id.monthRankedList);
        loading = view.findViewById(R.id.MonthRankedLoading);

        adapter = new CZBooksBookListAdapter(getActivity(),dataList);

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


        return view;
    }
    public void getBookList() {
        String url = getArguments().getString("classification");

        new Thread(() -> {
            try {
                List<CZBooksClassification> temp = CZBooks.getBookList(url,page);
                if(temp != null) {
                    dataList.addAll(temp);
                    getActivity().runOnUiThread(() -> updateUI());
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