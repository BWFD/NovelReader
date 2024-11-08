package com.example.novelreader.hjwzw;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.novelreader.R;
import com.example.novelreader.dao.hjwzwClassification;
import com.example.novelreader.service.hjwzw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class hjwzwSearchFragment extends Fragment {

    View view;

    private ListView listView;
    private List<hjwzwClassification> dataList = new ArrayList<>();
    private boolean notLoading;
    private int page;
    EditText editText;
    hjwzwBooksAdapter adapter;
    TextView noFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hjwzw_search, container, false);

        notLoading = false;
        page = 1;

        listView = view.findViewById(R.id.searchList);
        listView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        editText = view.findViewById(R.id.searchText);

        adapter = new hjwzwBooksAdapter(getActivity(),dataList);

        noFound = view.findViewById(R.id.NoFound);

        listView.setAdapter(adapter);

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
                dataList.clear();
                adapter.notifyDataSetChanged();
                listView.invalidateViews();
                getListAndUpdate();
                notLoading = false;
                page = 1;
                // 執行搜尋操作
                return true;
            }
            return false;
        });


        Button submitButton = view.findViewById(R.id.searchSubmitButton);
        submitButton.setOnClickListener(view -> {

            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
            noFound.setVisibility(View.INVISIBLE);
            dataList.clear();
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
            getListAndUpdate();
            notLoading = false;
            page = 1;
        });

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
                        getListAndUpdate();
                    }
                }
            }
        });

        return view;
    }

    public void getListAndUpdate() {
        String keyword = String.valueOf(editText.getText());
        String url = "Channel/" + keyword;

        new Thread(() -> {
            try {
                List<hjwzwClassification> temp = hjwzw.getBookList(url,page);
                if(temp != null) {
                    dataList.addAll(temp);
                    getActivity().runOnUiThread(() -> updateUI());
                }
                if(temp == null && page ==1) {
                    getActivity().runOnUiThread(() -> {
                        noFound.setVisibility(View.VISIBLE);
                    });
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void updateUI() {
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
        listView.invalidateViews();
        page = page + 1;
        notLoading = true;
    }
}