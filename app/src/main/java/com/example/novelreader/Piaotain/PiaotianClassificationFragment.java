package com.example.novelreader.Piaotain;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.novelreader.ErrorPage;
import com.example.novelreader.NetworkError;
import com.example.novelreader.R;
import com.example.novelreader.dao.PiaotianClassification;
import com.example.novelreader.service.NetworkUtil;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.List;

public class PiaotianClassificationFragment extends Fragment {

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 為此 Fragment 加載佈局
        view = inflater.inflate(R.layout.fragment_piaotian_classification, container, false);
        if(isAdded() && getContext() != null) {
            getClassification(getActivity());
        }
        return view;
    }
    public void getClassification(Context context) {
        new Thread(new Runnable() {
            List<PiaotianClassification> classificationList;
            @Override
            public void run() {
                try {
                    classificationList = Piaotian.getClassification(context);
                } catch (IOException e) {
                    Intent intent = new Intent(getContext(), ErrorPage.class);
                    startActivity(intent);
                    return;
                }
                if(isAdded() && getContext() != null) {
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createButtons(classificationList);
                        }
                    });
                }
            }
        }).start();
    }

    public void createButtons(List<PiaotianClassification> classificationList) {
        TextView loading = view.findViewById(R.id.loading);
        loading.setVisibility(View.INVISIBLE);

        LinearLayout layout = view.findViewById(R.id.classificationList);

        layout.setLayoutTransition(null);
        for (PiaotianClassification classification : classificationList) {
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
                Intent intent = new Intent(getActivity(), PiaotianClassificationListActivity.class);
                intent.putExtra("URL",classification.getHtml());
                intent.putExtra("title","飄天文學網 - " + classification.getName());
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