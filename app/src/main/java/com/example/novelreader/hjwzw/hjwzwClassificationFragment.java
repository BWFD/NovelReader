package com.example.novelreader.hjwzw;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.novelreader.CZBooks.CZBooksClassificationList3Activity;
import com.example.novelreader.CZBooks.CZBooksClassificationList5Activity;
import com.example.novelreader.ErrorPage;
import com.example.novelreader.NetworkError;
import com.example.novelreader.R;
import com.example.novelreader.dao.CZBooksClassification;
import com.example.novelreader.dao.hjwzwClassification;
import com.example.novelreader.service.NetworkUtil;
import com.example.novelreader.service.hjwzw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class hjwzwClassificationFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hjwzw_classification, container, false);

        if (!NetworkUtil.isNetworkAvailable(getContext())) {
            Intent intent = new Intent(getContext(), NetworkError.class);
            startActivity(intent);
        }

        getClassification();
        return view;
    }

    public void getClassification() {
        new Thread(new Runnable() {
            List<hjwzwClassification> classificationList = new ArrayList<>();
            @Override
            public void run() {
                try {
                    classificationList = hjwzw.getClassification();
                } catch (IOException e) {
                    Intent intent = new Intent(getContext(), ErrorPage.class);
                    startActivity(intent);
                    return;
                }
                if(isAdded() && getContext() != null) {
                    requireActivity().runOnUiThread(() -> createButtons(classificationList));
                }
            }
        }).start();
    }

    public void createButtons(List<hjwzwClassification> classificationList) {
        TextView loading = view.findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);

        LinearLayout layout = view.findViewById(R.id.classificationList);

        layout.setLayoutTransition(null);
        for (hjwzwClassification classification : classificationList) {

            // 創建一個新的按鈕
            Button button = new Button(getActivity());
            button.setText(classification.getName());
            button.setTextColor(Color.WHITE);
            button.setTextSize(20);
            button.setBackgroundColor(Color.TRANSPARENT);

            // 設置按鈕的布局參數
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            button.setLayoutParams(params);
            // 設置按鈕的點擊事件
            button.setOnClickListener(v -> {

                if (!NetworkUtil.isNetworkAvailable(getContext())) {
                    Intent intent = new Intent(getContext(), NetworkError.class);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(getActivity(), hjwzwBookListActivity.class);

                intent.putExtra("URL",classification.getUrl());
                intent.putExtra("title",classification.getName());
                startActivity(intent);
            });
            layout.addView(button);

            View bar = new View(getActivity());
            int color = ContextCompat.getColor(requireActivity(), R.color.light_blue_600);
            bar.setBackgroundColor(color);
            params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    6
            );
            bar.setLayoutParams(params);
            layout.addView(bar);
        }
        layout.requestLayout();
    }
}