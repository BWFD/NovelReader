package com.example.novelreader;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.novelreader.CZBooks.CZBooksInfoActivity;
import com.example.novelreader.Piaotain.PiaotianInfoActivity;
import com.example.novelreader.hjwzw.hjwzwInfoActivity;


public class NovelWebSiteFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_novel_web_site, container, false);

        Button piaotian = view.findViewById(R.id.piaotian);
        Button czBooks = view.findViewById(R.id.CZBooks);
        Button hjwzw = view.findViewById(R.id.hjwzw);
        Button setting = view.findViewById(R.id.setting);
        Button errorTest = view.findViewById(R.id.errorTest);

        piaotian.setOnClickListener(view -> {

            //getContext().getContentResolver().delete(uri,null,null);
            Intent intent = new Intent(getActivity(), PiaotianInfoActivity.class);
            startActivity(intent);
        });

        czBooks.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), CZBooksInfoActivity.class);
            startActivity(intent);
        });

        hjwzw.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), hjwzwInfoActivity.class);
            startActivity(intent);
        });

        setting.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), WordSettingActivity.class);
            startActivity(intent);
        });

        errorTest.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ErrorTestActivity.class);
            startActivity(intent);
        });

        return view;
    }
}