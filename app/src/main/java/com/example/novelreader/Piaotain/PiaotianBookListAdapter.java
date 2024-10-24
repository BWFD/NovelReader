package com.example.novelreader.Piaotain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.novelreader.R;
import com.example.novelreader.dao.PiaotianClassification;

import java.util.List;

public class PiaotianBookListAdapter extends ArrayAdapter<PiaotianClassification> {
    Context context;
    public PiaotianBookListAdapter(Activity context, List<PiaotianClassification> dataList) {
        super(context, 0, dataList);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        //listItemView可能會是空的，例如App剛啟動時，沒有預先儲存的view可使用
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_with_buttons, parent, false);
        }

        //找到data，並在View上設定正確的data
        PiaotianClassification item = getItem(position);


        Button bookButton = listItemView.findViewById(R.id.Bookbutton);
        bookButton.setText(item.getName() + "    作者: " + item.getAuthor());
        bookButton.setGravity(Gravity.CENTER_VERTICAL);
        bookButton.setTextSize(20);
        bookButton.setPadding(10,0,0,0);
        bookButton.setOnClickListener(view -> {
            Intent intent = new Intent(context, PiaotianBookInfoActivity.class);
            intent.putExtra("URL",item.getHtml());
            context.startActivity(intent);
        });


        return listItemView;
    }
}
