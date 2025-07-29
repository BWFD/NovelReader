package com.example.novelreader;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.novelreader.CZBooks.CZBooksBookInfoActivity;
import com.example.novelreader.Piaotain.PiaotianBookInfoActivity;
import com.example.novelreader.hjwzw.hjwzwBookInfoActivity;
import com.example.novelreader.service.Piaotian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class BookMarkFragment extends Fragment {
    View view;
    private LinearLayout bookmarkList;
    Uri uri = Uri.parse("content://com.example.novelreader.bookmark/data");

    Uri download = Uri.parse("content://com.example.novelreader.download/data");
    Cursor cursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_book_mark, container, false);
        bookmarkList = view.findViewById(R.id.bookmarkList);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }


    public void update() {
        bookmarkList.removeAllViews();
        cursor = getContext().getContentResolver().query(uri, null, null, null, "_id DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String text = "";
                String url = "";
                String bookName = "";
                String TOTALHTML = "";
                String webSite = "";
                int scrolled = 0;
                long id = 0;
                Button button = new Button(getActivity());
                int index = cursor.getColumnIndex("bookName");
                if (index != -1) {
                    text = cursor.getString(index);
                    bookName = text;
                }

                index = cursor.getColumnIndex("webSite");
                if (index != -1) {
                    webSite = cursor.getString(index);
                    if(Objects.equals(webSite, "Piaotian")) {
                        text = text + " － " + "飄天文學網";
                    }
                    else
                    if(Objects.equals(webSite, "CZBooks")) {
                        text = text + " － " + "小說狂人";
                    }
                    else
                    if(Objects.equals(webSite, "hjwzw")) {
                        text = text + " － " + "黃金屋中文網";
                    }
                    else  {
                        text = text + "-" + webSite;
                    }
                }

                index = cursor.getColumnIndex("chapterName");
                if (index != -1) {
                    text = text + "\n" + cursor.getString(index);
                }

                button.setText(text);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                button.setLayoutParams(params);
                button.setGravity(Gravity.CENTER_VERTICAL);
                button.setPadding(10, 0, 0, 0);
                button.setTextColor(Color.WHITE);
                button.setBackgroundColor(Color.TRANSPARENT);

                index = cursor.getColumnIndex("chapterUrl");
                if (index != -1) {
                    url = cursor.getString(index);
                }
                String finalUrl = url;
                String finalBookName = bookName;
                String finalWebSite = webSite;

                index = cursor.getColumnIndex("TOTALHTML");
                if (index != -1) {
                    TOTALHTML = cursor.getString(index);
                }

                index = cursor.getColumnIndex("Scrolled");
                if (index != -1) {
                    scrolled = cursor.getInt(index);
                }

                index = cursor.getColumnIndex("_id");
                if (index != -1) {
                    id = cursor.getInt(index);
                }

                String finalTOTALHTML = TOTALHTML;
                int finalScrolled = scrolled;
                long finialId = id;
                button.setOnClickListener(view -> {
                    Intent intent = new Intent(getActivity(),ReaderActivity.class);
                    intent.putExtra("currentHtml", finalUrl);
                    intent.putExtra("TOTALHTML",finalTOTALHTML);
                    intent.putExtra("isInBookMark","true");
                    intent.putExtra("bookName", finalBookName);
                    intent.putExtra("webSite", finalWebSite);
                    intent.putExtra("scrolled", finalScrolled);
                    intent.putExtra("id",finialId);
                    startActivity(intent);
                });


                button.setLongClickable(true);
                button.setOnLongClickListener(view -> {
                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.bookmarkmenu, popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(item -> {
                        int popupMenuId = item.getItemId();
                        if(popupMenuId == R.id.deleteMark) {
                            getContext().getContentResolver().delete(uri,"TOTALHTML = ?", new String[]{finalTOTALHTML});
                            getContext().getContentResolver().notifyChange(uri, null);
                            update();
                            return true;
                        }
                        else
                        if(popupMenuId == R.id.openBook) {
                            Intent intent;
                            if(finalWebSite.equals("Piaotian")) {
                                intent = new Intent(getActivity(), PiaotianBookInfoActivity.class);
                                intent.putExtra("URL","CHAPTER:" + finalUrl);
                                startActivity(intent);
                            }
                            else
                            if(finalWebSite.equals("CZBooks")) {
                                intent = new Intent(getActivity(), CZBooksBookInfoActivity.class);
                                intent.putExtra("URL","CHAPTER:" + finalUrl);
                                startActivity(intent);
                            }
                            else
                            if(finalWebSite.equals("hjwzw")) {
                                intent = new Intent(getActivity(), hjwzwBookInfoActivity.class);
                                intent.putExtra("URL","CHAPTER:" + finalUrl);
                                startActivity(intent);
                            }
                            return true;
                        }
                        else
                        if(popupMenuId == R.id.editPosition) {
                            Intent intent = new Intent(getActivity(), BookMarkEditPositionActivity.class);
                            intent.putExtra("id",finialId);

                            startActivity(intent);
                            return true;
                        }
                        else
                        if(popupMenuId == R.id.deleteDownload) {
                            getContext().getContentResolver().delete(download,"TOTALHTML = ?", new String[]{finalTOTALHTML});
                            getContext().getContentResolver().notifyChange(download, null);
                            Toast.makeText(getContext(), "已刪除預載", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else {
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true; // 返回 true 表示事件已處理，阻止單擊事件發生
                });

                bookmarkList.addView(button);

                View bar = new View(getActivity());
                int color = ContextCompat.getColor(requireActivity(), R.color.light_blue_600);
                bar.setBackgroundColor(color);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        6
                );
                bar.setLayoutParams(params);

                bookmarkList.addView(bar);
            }
        }
        cursor.close();
        bookmarkList.invalidate();
    }
}