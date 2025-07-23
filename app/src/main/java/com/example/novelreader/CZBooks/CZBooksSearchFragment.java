package com.example.novelreader.CZBooks;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.LruCache;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.novelreader.ErrorPage;
import com.example.novelreader.NetworkError;
import com.example.novelreader.Piaotain.PiaotianBookInfoActivity;
import com.example.novelreader.R;
import com.example.novelreader.dao.CZBooksClassification;
import com.example.novelreader.dao.PiaotianClassification;
import com.example.novelreader.service.CZBooks;
import com.example.novelreader.service.NetworkUtil;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CZBooksSearchFragment extends Fragment {

    View view;

    private ListView listView;
    private List<CZBooksClassification> dataList= new ArrayList<>();
    private boolean notLoading;
    private int page;
    EditText editText;
    CZBooksBookListAdapter adapter;
    TextView noFound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_c_z_books_search, container, false);

        notLoading = false;
        page = 1;

        listView = view.findViewById(R.id.searchList);
        listView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

        editText = view.findViewById(R.id.searchText);

        adapter = new CZBooksBookListAdapter(getActivity(),dataList);

        listView.setAdapter(adapter);

        noFound = view.findViewById(R.id.NoFound);

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

            if (!NetworkUtil.isNetworkAvailable(getContext())) {
                Intent intent = new Intent(getContext(), NetworkError.class);
                startActivity(intent);
                return;
            }

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
        String url = "https://czbooks.net/s/" + keyword;

        new Thread(() -> {
            try {
                List<CZBooksClassification> temp = CZBooks.getBookList(url,page);
                if(temp != null) {
                    dataList.addAll(temp);
                    getActivity().runOnUiThread(() -> updateUI());
                }
                if(temp == null && page == 1) {
                    getActivity().runOnUiThread(() ->{
                        noFound.setVisibility(View.VISIBLE);
                    });
                }
            } catch (IOException e) {
                Intent intent = new Intent(getContext(), ErrorPage.class);
                startActivity(intent);
            }
        }).start();
    }

    public void updateUI() {
        adapter.notifyDataSetChanged();
        listView.setVisibility(View.VISIBLE);
        listView.setSelection(listView.getScrollY());
        listView.invalidateViews();
        page = page + 1;
        notLoading = true;
    }
}