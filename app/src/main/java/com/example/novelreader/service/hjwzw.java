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

import com.example.novelreader.dao.hjwzwBookDetail;
import com.example.novelreader.dao.hjwzwClassification;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class hjwzw {
    private static OkHttpClient client = new OkHttpClient();
    private static Uri download = Uri.parse("content://com.example.novelreader.download/data");

    private static int retrySec = 0;

    public static List<hjwzwClassification> getClassification() throws IOException {
        String url = "https://tw.hjwzw.com/";
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
        Elements elements = document.select("span.index2a a");
        List<hjwzwClassification> dataList = new ArrayList<>();
        for(Element e : elements) {
            if(e.text().equals("首 頁") || e.text().equals("手機版") ||
                    e.text().equals("移動版") || e.text().equals("書架") ||
            e.text().equals("情趣用品")) {
                continue;
            }
            dataList.add(new hjwzwClassification(e.text(), e.attr("href"), "NULL", "NULL"));
        }

        return dataList;
    }

    public static List<hjwzwClassification> getBookList(String url, int page) throws IOException  {
        List<hjwzwClassification> bookList = new ArrayList<>();
        String basicURL = "https://tw.hjwzw.com/";
        url = url.replace("Channel","List");
        String encoded = URLEncoder.encode(url, "UTF-8");
        String URL = basicURL + encoded + "__" + page;
        System.out.println(URL);
        Request request = new Request.Builder()
                .url(URL)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
        } else {
            System.out.println("Request failed with code: " + response.code());
        }
        Elements elements = document.select("div.wd4 a");
        int maxPage = 1 ;
        if(elements.isEmpty()) return null;
        String []temp = elements.get(elements.size()-1).attr("href").split("_");
        if(temp[temp.length-1].matches("\\d+")) {
            maxPage = Integer.parseInt(temp[temp.length-1]);
        }

        if(page > maxPage) {
            return null;
        }

        elements = document.select("tbody tr td table tbody tr td");
        if(elements.isEmpty()) return null;
        for(int i = 0; i < elements.size(); i+=1) {
            if (elements.get(i).select("div a img").attr("src").equals("")) {
                continue;
            }
            bookList.add(new hjwzwClassification(elements.get(i).select("div a img").attr("alt"),
                    basicURL + elements.get(i+2).select("span div a").attr("href"),
                    basicURL + elements.get(i).select("div a img").attr("src"),
                    elements.get(i+2).select("span.wd7 a").attr("title")));
        }
        return  bookList;
    }

    public static hjwzwBookDetail getBookInfo(String url) throws IOException {
        String basicURL = "https://tw.hjwzw.com";

        if(url.startsWith("CHAPTER:")) {
            url = basicURL + url.replace("CHAPTER:","").split(",")[0].replace("/Read","");
        }
        System.out.println(url);
        hjwzwBookDetail bookDetail = new hjwzwBookDetail();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
        }else {
            System.out.println("Request failed with code: " + response.code());
        }
        Elements elements = document.select("h1");
        bookDetail.setName(elements.get(0).text());

        elements = document.select("table tbody tr td table tbody tr td div");
        bookDetail.setAuthor("作者 : " + elements.select("a[title*=作者標簽]").first().text());
        String temp[] = elements.text().split("【內容簡介】");
        bookDetail.setDesc(temp[1]);

        elements = document.select("table tbody tr td table tbody tr td div img");
        bookDetail.setImageURL(basicURL + elements.get(0).attr("src"));


        List<String> name = new ArrayList<>();
        List<String> html = new ArrayList<>();

        url = url.replace("Book", "Book/Chapter");
        request = new Request.Builder()
                .url(url)
                .build();
        document = null;
        response = client.newCall(request).execute();
        if(response.isSuccessful()){
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
        }else {
            System.out.println("Request failed with code: " + response.code());
        }
        elements = document.select("div[id=tbchapterlist] table tbody tr td a");
        elements.forEach(e ->{
            name.add(e.text());
            html.add(e.attr("href"));
        });

        bookDetail.setChapterName(name);
        bookDetail.setChapterHTML(html);

        return bookDetail;
    }

    public static String[] getChapter(String url) throws IOException {
        String[] book = new String[2];
        String basicURL = "https://tw.hjwzw.com";
        Request request = new Request.Builder()
                .url(basicURL + url)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();
        if(response.isSuccessful()){
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
        }else {
            System.out.println("Request failed with code: " + response.code());
            System.out.println(response.header("Retry-After"));
            if(response.header("Retry-After") != null || !Objects.equals(response.header("Retry-After"), "0")) {
                retrySec = Integer.parseInt(response.header("Retry-After"));
            }
            return null;
        }
        Elements elements = document.select("h1");
        book[0] = elements.text();

        elements = document.select("table tbody tr td div[style=font-size: 20px; line-height: 30px; word-wrap: break-word; table-layout: fixed; word-break: break-all; width: 750px; margin: 0 auto; text-indent: 2em;]");
        StringBuilder html = new StringBuilder(elements.get(0).html());
        String temp[] = html.toString().split("</p>");
        html = new StringBuilder();
        for (int i = 2; i < temp.length; i++) {
            if(temp[i].isEmpty() || temp[i].equals("<p>") || temp[i].equals(" \n<br>\n<p>")) {
                continue;
            }

            html.append(temp[i].replace("<p>", "\n").replace("<br>", ""));
        }
        book[1] = html.toString();
        return book;
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
        try {
            for(int i=0;i<max;i++) {

                Cursor cursor = activity.getContentResolver().query(download,null,"chapterUrl = ?",new String[]{TOTALHTML.get(i)},null);
                if(cursor.getCount() > 0) {
                    continue;
                }

                String[] book = null;
                while(book == null){
                    book = getChapter(TOTALHTML.get(i));
                    Thread.sleep(retrySec * 1000L);
                }
                retrySec = 0;
                ContentValues values = new ContentValues();
                values.put("chapterName", book[0]);
                values.put("novel", book[1]);
                values.put("scrolled", 0);
                values.put("website", "hjwzw");
                values.put("TOTALHTML", encode);
                values.put("chapterUrl", TOTALHTML.get(i));
                activity.getContentResolver().insert(download, values);
                int progress = i + 1;
                builder.setContentText("進度：" + progress + "/" + max);
                Log.d("Progress", "Progress: " + progress + "/" + max);
                builder.setProgress(max, progress, false);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
            builder.setContentText("預載完成")
                    .setProgress(0, 0, false)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (Exception e) {
            throw new Exception("預載失敗");
        }
    }
}
