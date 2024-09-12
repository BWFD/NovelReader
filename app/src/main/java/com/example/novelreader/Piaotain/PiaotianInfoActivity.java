package com.example.novelreader.Piaotain;

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

public class PiaotianInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_piaotian_info);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        PiaotianFragmentAdapter fragmentAdapter = new PiaotianFragmentAdapter(this);
        viewPager.setAdapter(fragmentAdapter);


        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("分類推薦");
                    break;
                case 1:
                    tab.setText("月推薦榜");
                    break;
                case 2:
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