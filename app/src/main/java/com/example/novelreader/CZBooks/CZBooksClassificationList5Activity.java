package com.example.novelreader.CZBooks;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.novelreader.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CZBooksClassificationList5Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_czbooks_classification_list5);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        String Url = getIntent().getStringExtra("URL");
        CZBooksClassification5Adapter fragmentAdapter = new CZBooksClassification5Adapter(this,Url);
        viewPager.setAdapter(fragmentAdapter);

        TextView title = findViewById(R.id.CZBooksClassificationTitle);
        title.setText(getIntent().getStringExtra("title"));

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("最新");
                    break;
                case 1:
                    tab.setText("日排行");
                    break;
                case 2:
                    tab.setText("周排行");
                    break;
                case 3:
                    tab.setText("總排行");
                    break;
                case 4:
                    tab.setText("完本");
                    break;

            }
        }).attach();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                // 設置自定義視圖
                TextView textView = new TextView(this);
                textView.setText(tab.getText());
                textView.setTextSize(16); // 設置字體大小
                textView.setTextColor(Color.WHITE); // 設置文字顏色（可選）
                textView.setGravity(Gravity.CENTER);
                tab.setCustomView(textView);
            }
        }
    }
}