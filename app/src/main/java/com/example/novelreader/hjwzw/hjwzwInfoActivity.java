package com.example.novelreader.hjwzw;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.novelreader.CZBooks.CZBooksFragmentAdapter;
import com.example.novelreader.NetworkError;
import com.example.novelreader.R;
import com.example.novelreader.service.NetworkUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class hjwzwInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hjwzw_info);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        hjwzwFragmentAdapter fragmentAdapter = new hjwzwFragmentAdapter(this);
        viewPager.setAdapter(fragmentAdapter);

        if (!NetworkUtil.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, NetworkError.class);
            startActivity(intent);
            finish();
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("分類菜單");
                    break;
                case 1:
                    tab.setText("搜尋");
                    break;
            }
        }).attach();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                // 設置自定義視圖
                TextView textView = new TextView(this);
                textView.setText(tab.getText());
                textView.setTextSize(18); // 設置字體大小
                textView.setTextColor(Color.WHITE); // 設置文字顏色（可選）
                textView.setGravity(Gravity.CENTER);
                tab.setCustomView(textView);
            }
        }
    }
}