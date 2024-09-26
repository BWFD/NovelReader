package com.example.novelreader.CZBooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.example.novelreader.Piaotain.PiaotianBookInfoActivity;
import com.example.novelreader.R;
import com.example.novelreader.dao.CZBooksClassification;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CZBooksBookListAdapter extends ArrayAdapter<CZBooksClassification> {
    Context context;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    //private List<String> imageUrls;
    private Set<String> imageUrls = new HashSet<>();
    private LruCache<String, Bitmap> imageCache;

    public CZBooksBookListAdapter(Activity context, List<CZBooksClassification> dataList) {
        super(context, 0, dataList);
        this.context = context;

        // 設置 LruCache 大小（使用可用內存的一部分作為緩存）
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8; // 使用最大內存的1/8
        imageCache = new LruCache<>(cacheSize);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        ViewHolder holder;

        //listItemView可能會是空的，例如App剛啟動時，沒有預先儲存的view可使用
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_button_image, parent, false);
            holder = new ViewHolder();
            holder.imageView = listItemView.findViewById(R.id.ListImage);
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        //找到data，並在View上設定正確的data
        CZBooksClassification item = getItem(position);
        String imageUrl = item.getImage();
        Bitmap bitmap = imageCache.get(imageUrl);
        if (bitmap != null) {
            // 如果圖片已在緩存中，直接使用
            holder.imageView.setImageBitmap(bitmap);
        } else {
            // 沒有緩存時，加載圖片
            holder.imageView.setImageResource(R.drawable.ic_launcher_round); // 設置預設圖片
            loadImage(imageUrl, holder.imageView);
            imageUrls.add(imageUrl);
        }


        Button bookButton = listItemView.findViewById(R.id.Bookbutton);
        bookButton.setText(item.getName() + "    " + item.getAuthor());
        bookButton.setGravity(Gravity.CENTER_VERTICAL);
        bookButton.setTextSize(20);
        bookButton.setPadding(10,0,0,0);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CZBooksBookInfoActivity.class);
                intent.putExtra("URL",item.getUrl());
                context.startActivity(intent);
            }
        });

        return listItemView;
    }
    private void loadImage(String imageUrl, ImageView imageView) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 下載圖片
                    URL url = new URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    final Bitmap bitmap = BitmapFactory.decodeStream(input);

                    if (bitmap != null) {
                        // 將下載的圖片添加到緩存
                        imageCache.put(imageUrl, bitmap);

                        // 主線程更新 UI
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    static class ViewHolder {
        ImageView imageView;
    }
}
