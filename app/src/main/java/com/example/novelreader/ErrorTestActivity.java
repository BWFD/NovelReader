package com.example.novelreader;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class ErrorTestActivity extends AppCompatActivity {

    Activity activity = this;
    private Button piaotian;
    private Button czbooks;
    private Button hjwzw;
    private Button deleteAllDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_error_test);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        piaotian = findViewById(R.id.piaotian);
        czbooks = findViewById(R.id.czbooks);
        hjwzw = findViewById(R.id.hjwzw);
        deleteAllDownload = findViewById(R.id.deleteAllDownload);
        piaotian.setOnClickListener(view -> {
            String url = "https://www.piaotia.com/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            try {
                Intent chooser = Intent.createChooser(intent, "選擇瀏覽器開啟網站");
                startActivity(chooser);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "網站開啟失敗", Toast.LENGTH_SHORT).show();
            }

        });

        czbooks.setOnClickListener(view -> {
            String url = "https://czbooks.net/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            try {
                Intent chooser = Intent.createChooser(intent, "選擇瀏覽器開啟網站");
                startActivity(chooser);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "網站開啟失敗", Toast.LENGTH_SHORT).show();
            }

        });

        hjwzw.setOnClickListener(view -> {
            String url = "https://tw.hjwzw.com/";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            try {
                Intent chooser = Intent.createChooser(intent, "選擇瀏覽器開啟網站");
                startActivity(chooser);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "網站開啟失敗", Toast.LENGTH_SHORT).show();
            }
        });

        deleteAllDownload.setOnClickListener(view -> {
            Uri uri = Uri.parse("content://com.example.novelreader.download/data");
            getContentResolver().delete(uri, null, null);
            Toast.makeText(activity, "已刪除所有下載", Toast.LENGTH_SHORT).show();
        });

    }
}