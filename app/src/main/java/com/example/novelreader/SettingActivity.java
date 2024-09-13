package com.example.novelreader;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {

    private TextView preview;

    private SeekBar sizeSelect;
    private TextView sizeValue;

    private int r = 170;
    private int g = 170;
    private int b = 170;


    private SeekBar RSelect;
    private TextView RValue;

    private SeekBar GSelect;
    private TextView GValue;

    private SeekBar BSelect;
    private TextView BValue;

    private Button resetButton;
    private Button confirm_button;

    Uri uri = Uri.parse("content://com.example.novelreader.setting/data");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preview = findViewById(R.id.textPreview);
        sizeSelect = findViewById(R.id.SizeSlecter);
        sizeValue = findViewById(R.id.SizeValue);

        RSelect = findViewById(R.id.RSelect);
        RValue = findViewById(R.id.RValue);

        GSelect = findViewById(R.id.GSelect);
        GValue = findViewById(R.id.GValue);

        BSelect = findViewById(R.id.BSelect);
        BValue = findViewById(R.id.BValue);

        resetButton = findViewById(R.id.ResetSetting);
        confirm_button = findViewById(R.id.confirm_button);

        sizeSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
               preview.setTextSize(15 + i);
               sizeValue.setText(String.valueOf(i + 1));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        RSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
                RValue.setText(String.valueOf(i));
                r = i;
                preview.setTextColor(Color.parseColor(String.format("#%02X%02X%02X", r, g, b)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        GSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
                GValue.setText(String.valueOf(i));
                g = i;
                preview.setTextColor(Color.parseColor(String.format("#%02X%02X%02X", r, g, b)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        BSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
                BValue.setText(String.valueOf(i));
                b = i;
                preview.setTextColor(Color.parseColor(String.format("#%02X%02X%02X", r, g, b)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sizeSelect.setProgress(0);
                RSelect.setProgress(170);
                GSelect.setProgress(170);
                BSelect.setProgress(170);

            }
        });

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                int Size = Integer.parseInt(String.valueOf(sizeSelect.getProgress())) +15;
                values.put("textSize",String.valueOf(Size));
                values.put("textColor",String.format("#%02X%02X%02X", r, g, b));
                String[] selectionArgs = new String[]{"only"};
                getContentResolver().update(uri,values,"only = ?",selectionArgs);

                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String textSize = "0";
        String textColor = "#AAAAAA";
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor!=null) {
            while(cursor.moveToNext()) {
                int index = cursor.getColumnIndex("textSize");
                if(index!=-1) {
                    textSize = cursor.getString(index);
                }
                index = cursor.getColumnIndex("textColor");
                if(index!=-1) {
                    textColor = cursor.getString(index);
                }
            }
        }
        cursor.close();
        sizeSelect.setProgress(Integer.parseInt(textSize)-15);
        String r = textColor.split("")[1] + textColor.split("")[2];
        String g = textColor.split("")[3] + textColor.split("")[4];
        String b = textColor.split("")[5] + textColor.split("")[6];

        RSelect.setProgress(Integer.parseInt(r,16));
        GSelect.setProgress(Integer.parseInt(g,16));
        BSelect.setProgress(Integer.parseInt(b,16));

    }
}