package com.example.novelreader.CZBooks;

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

import com.example.novelreader.Piaotain.PiaotianClassificationListActivity;
import com.example.novelreader.R;
import com.example.novelreader.dao.CZBooksClassification;
import com.example.novelreader.dao.PiaotianClassification;
import com.example.novelreader.service.CZBooks;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CZBooksClassificationFragment extends Fragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_c_z_books_classification, container, false);
        getClassification();
        return view;
    }

    public void getClassification() {
        new Thread(new Runnable() {
            List<CZBooksClassification> classificationList = new ArrayList<>();
            @Override
            public void run() {
                try {
                    classificationList = CZBooks.getClassification();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(isAdded() && getContext() != null) {
                    requireActivity().runOnUiThread(() -> createButtons(classificationList));
                }
            }
        }).start();
    }

    public void createButtons(List<CZBooksClassification> classificationList) {
        TextView loading = view.findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);

        LinearLayout layout = view.findViewById(R.id.classificationList);

        layout.setLayoutTransition(null);
        for (CZBooksClassification classification : classificationList) {
            if(classification.getName().equals("作者專欄")) {
                continue;
            }
            // 創建一個新的按鈕
            Button button = new Button(getActivity());
            button.setText(classification.getName());
            button.setTextColor(Color.WHITE);
            button.setTextSize(20);
            button.setBackgroundColor(Color.TRANSPARENT);

            int activityType = 5;

            if(classification.getName().equals("精選排行") ||
                    classification.getName().equals("人氣榜") ||
                    classification.getName().equals("收藏榜") ||
                    classification.getName().equals("完本榜") ||
                    classification.getName().equals("工口榜")) {
                activityType = 3;//3
            }
            else {
                activityType = 5;
            }

            // 設置按鈕的布局參數
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            button.setLayoutParams(params);
            // 設置按鈕的點擊事件
            int finalActivityType = activityType;
            button.setOnClickListener(v -> {
                Intent intent;
                if(finalActivityType == 5) {
                    intent = new Intent(getActivity(), CZBooksClassificationList5Activity.class);
                }
                else
                if(finalActivityType == 3) {
                    intent = new Intent(getActivity(), CZBooksClassificationList3Activity.class);
                }
                else {
                    intent = null;
                }
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