package com.example.novelreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ChapterListAdapter extends ArrayAdapter<String> {
    Context context;
    List<String> name;
    List<String> html;
    Uri uri = Uri.parse("content://com.example.novelreader.bookmark/data");
    Cursor cursor;
    String bookName;
    String webSite;

    public ChapterListAdapter(Activity context, List<String> name, List<String> html, String bookName, String webSite) {
        super(context, 0, name);
        this.context = context;
        this.name = name;
        this.html = html;
        this.cursor = context.getContentResolver().query(uri, null, null, null, null);
        this.bookName = bookName;
        this.webSite = webSite;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        //listItemView可能會是空的，例如App剛啟動時，沒有預先儲存的view可使用
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_with_buttons, parent, false);
        }

        String name = getItem(position);

        Button button = listItemView.findViewById(R.id.Bookbutton);
        button.setText(name);
        button.setGravity(Gravity.CENTER_VERTICAL);
        button.setTextSize(18);
        button.setPadding(10,0,0,0);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReaderActivity.class);
                intent.putStringArrayListExtra("TOTALHTML",new ArrayList<>(html));
                intent.putExtra("currentHtml", html.get(position));
                intent.putExtra("isInBookMark","false");
                intent.putExtra("bookName", bookName);
                intent.putExtra("webSite",webSite);

                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int index = cursor.getColumnIndex("TOTALHTML");
                        if(index != -1) {
                            String TOTALHTML = TextUtils.join(",", new ArrayList<>(html));
                            if(cursor.getString(index).equals(TOTALHTML)) {
                                intent.putExtra("isInBookMark", "true");
                            }
                        }
                    }
                }
                cursor.close();
                context.startActivity(intent);
            }
        });

        return listItemView;
    }
}
