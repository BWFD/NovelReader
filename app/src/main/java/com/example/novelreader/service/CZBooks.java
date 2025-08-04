package com.example.novelreader.service;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.novelreader.dao.CZBooksBookDetail;
import com.example.novelreader.dao.CZBooksClassification;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CZBooks {

    private static OkHttpClient client = new OkHttpClient();

    private static Uri download = Uri.parse("content://com.example.novelreader.download/data");

    public static List<CZBooksClassification> getClassification() throws IOException {
        String url = "https://czbooks.net/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Document document = null;
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
        } else {
            System.out.println("Request failed with code: " + response.code());
        }
        Elements elements = document.select("ul.nav.menu li a");
        List<CZBooksClassification> classificationList = new ArrayList<>();
        elements.forEach(e ->{
            classificationList.add(new CZBooksClassification(e.text(), e.attr("href"), "NULL", "NULL"));
        });

        return classificationList;
    }

    public static List<CZBooksClassification> getBookList(String url, int page) throws IOException {
        List<CZBooksClassification> booklist = new ArrayList<>();
        String URL = url + "/" + page;
        //System.out.println(URL);
        Request request = new Request.Builder()
                .url(URL)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);

            int size = document.select("ul.nav.paginate li").size() - 1;
            int maxPage = Integer.parseInt(document.select("ul.nav.paginate li").get(size).text().replace("~",""));
            if(page > maxPage) {
                return null;
            }


            Elements elements = document.select("ul.nav.novel-list div.novel-item");
            if(elements.isEmpty()) return null;
            elements.forEach(element -> {
                booklist.add(new CZBooksClassification(element.select("div.novel-item-title").text(),
                        element.select("a").attr("href"),
                        element.select("div.novel-item-thumbnail img").attr("src"),
                        element.select("div.novel-item-author").text()));
            });
        } else {
            System.out.println("Request failed with code: " + response.code());
        }

        return booklist;
    }


    public static CZBooksBookDetail getBookDetail(String url) throws IOException {
        if(url.startsWith("CHAPTER:")) {
            url = getBookInfoByChapter(url.replace("CHAPTER:",""));
        }
        CZBooksBookDetail czBooksBookDetail = new CZBooksBookDetail();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
            Elements elements = document.select("div.novel-detail");
            elements.forEach(element -> {
                czBooksBookDetail.setName(element.select("div.info-wrap div.info span.title").text());
                czBooksBookDetail.setAuthor(element.select("div.info-wrap div.info span.author").text());
                czBooksBookDetail.setDesc(element.select("div.info-wrap div.description").text());
                czBooksBookDetail.setImageURL(element.select("div.thumbnail img").attr("src"));
            });
            elements = document.select("ul.nav.chapter-list li");
            /*
            List<String> name = elements.stream().map(element -> element.text()).collect(Collectors.toList());
            List<String> html = elements.stream().map(element -> element.select("a").attr("href").replace("//","https://")).collect(Collectors.toList());
            */
            List<String> name = new ArrayList<>();
            List<String> html = new ArrayList<>();

            for(Element element : elements) {
                if(!element.select("a").attr("href").equals("")) {
                    name.add(element.text());
                    html.add("https:" + element.select("a").attr("href"));
                }
            }

            czBooksBookDetail.setChapterName(name);
            czBooksBookDetail.setChapterHTML(html);

            if(document.select("ul.nav.novel-detail-function-bar li").size() == 6) {
                czBooksBookDetail.setSuggestionHtml("https:" + document.select("ul.nav.novel-detail-function-bar li").get(3).select("a").attr("href"));
            }

        } else {
            System.out.println("Request failed with code: " + response.code());
        }

        return czBooksBookDetail;
    }

    public static String[] getChapter(String url) throws IOException {
        String []book = new String[2];
        Request request = new Request.Builder()
                .url(url)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()){
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
            Elements elements = document.select("div.content");
            book[0] = document.select("div.name").get(0).text();
            book[1]= "";
            Arrays.stream(elements.get(0).html().split("<br>")).forEach(str ->{
                if(str.equals(" \n")) return;
                book[1] = book[1] + str.replace("  ","").replace("　　","") + "\n";
            });
        }
        else {
            System.out.println("Request failed with code: " + response.code());
            System.out.println(response.header("Retry-After"));
            return null;
        }

        return book;
    }

    public static String getBookInfoByChapter(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        Document document = null;
        if(response.isSuccessful()){
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
        }
        else {
            System.out.println("Request failed with code: " + response.code());
        }
        return "https:" + document.select("div.position a").get(2).attr("href");
    }

    public static void download(Activity activity, String encode) throws Exception {
        NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        String decode = StrZipUtil.uncompress(encode);
        List<String> TOTALHTML = new ArrayList<>(List.of(decode.split("\\|")));
        int max = TOTALHTML.size();
        int NOTIFICATION_ID = 1001;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("download", "download", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, "download")
                .setContentTitle("小說預載中")
                .setContentText("初始化中...")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setProgress(max, 1, false);
        builder.setOngoing(true);
        try {
            for(int i=0;i<max;i++) {
                int progress = i + 1;
                builder.setContentText("進度：" + progress + "/" + max);
                Log.d("Progress", "Progress: " + progress + "/" + max);
                builder.setProgress(max, progress, false);
                notificationManager.notify(NOTIFICATION_ID, builder.build());

                Cursor cursor = activity.getContentResolver().query(download,null,"chapterUrl = ?",new String[]{TOTALHTML.get(i)},null);
                if(cursor.getCount() > 0) {
                    continue;
                }

                String[] book = null;
                while(book == null){
                    book = getChapter(TOTALHTML.get(i));
                    Thread.sleep(100);
                }
                ContentValues values = new ContentValues();
                values.put("chapterName", book[0]);
                values.put("novel", book[1]);
                values.put("scrolled", 0);
                values.put("website", "CZbooks");
                values.put("TOTALHTML", encode);
                values.put("chapterUrl", TOTALHTML.get(i));
                activity.getContentResolver().insert(download, values);
            }
            builder.setContentText("預載完成")
                    .setProgress(0, 0, false)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done);
            builder.setOngoing(false);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (Exception e) {
            builder.setOngoing(false);
            throw new Exception("預載失敗");
        }
    }
}
