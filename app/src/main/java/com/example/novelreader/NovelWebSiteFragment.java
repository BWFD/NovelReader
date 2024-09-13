package com.example.novelreader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.novelreader.Piaotain.PiaotianInfoActivity;


public class NovelWebSiteFragment extends Fragment {
    View view;

    Uri uri = Uri.parse("content://com.example.novelreader/data");
    Cursor cursor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_novel_web_site, container, false);

        Button piaotian = view.findViewById(R.id.piaotian);
        cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        piaotian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //getContext().getContentResolver().delete(uri,null,null);
                Intent intent = new Intent(getActivity(), PiaotianInfoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}