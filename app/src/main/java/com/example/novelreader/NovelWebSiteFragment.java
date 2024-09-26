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


public class NovelWebSiteFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_novel_web_site, container, false);

        Button piaotian = view.findViewById(R.id.piaotian);
        Button czBooks = view.findViewById(R.id.CZBooks);
        Button setting = view.findViewById(R.id.setting);

        piaotian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getContext().getContentResolver().delete(uri,null,null);
                Intent intent = new Intent(getActivity(), PiaotianInfoActivity.class);
                startActivity(intent);
            }
        });

        czBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CZBooksInfoActivity.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}