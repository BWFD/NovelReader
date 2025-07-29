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

import com.example.novelreader.dao.PiaotianBookDetail;
import com.example.novelreader.dao.PiaotianClassification;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Piaotian {

    private static OkHttpClient client = new OkHttpClient();

    private static Uri download = Uri.parse("content://com.example.novelreader.download/data");

    private static int retrySec = 0;

    public static List<PiaotianClassification> getClassification(Context context) throws IOException {
        String url = "https://www.piaotia.com/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Document document = null;
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            document = Jsoup.parse(response.body().byteStream(), "GBK", url);
        } else {
            System.out.println("Request failed with code: " + response.code());
        }

        Elements classification = document.select("div.navinner ul li a");
        List<PiaotianClassification> classificationList = new ArrayList<>();


        classification.forEach(element -> {
            if (!element.attr("href").endsWith("/")) {

                classificationList.add(new PiaotianClassification(
                        "https://www.piaotia.com" + element.attr("href"), TranslateUtil.chs2cht(element.text()), "NULL"));
            }
        });
        return classificationList;
    }

    public static List<PiaotianClassification> getMonthRank(int Page) throws IOException {
        List<PiaotianClassification> monthRank = new ArrayList<>();
        String url = "https://www.piaotia.com/booktopmonthvote/0/INDEX.html";
        url = url.replace("INDEX",String.valueOf(Page));

        Request request = new Request.Builder()
                .url(url)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            document = Jsoup.parse(response.body().byteStream(), "GBK", url);
            Elements urlList = document.select("table.grid tr");
            if(urlList.isEmpty()) {
                return null;
            }
            int MaxPage = Integer.parseInt(document.select("div.pagelink a.last").text());
            if(Page >= MaxPage + 1){
                return null;
            }
            urlList.forEach(element -> {
                Elements bookInfo = element.select("td");
                if (bookInfo.size() > 1) {
                    //bookName
                    //System.out.println(bookInfo.get(0).text());
                    //bookInfoUrl
                    //System.out.println("https://www.piaotia.com" + bookInfo.get(0).select("a").attr("href"));
                    //bookAuthor
                    //System.out.println(bookInfo.get(2).text());
                    monthRank.add(new PiaotianClassification(
                            "https://www.piaotia.com" + bookInfo.get(0).select("a").attr("href").replace("https://www.piaotia.com",""),
                            TranslateUtil.chs2cht(bookInfo.get(0).text()),
                            TranslateUtil.chs2cht(bookInfo.get(2).text())
                    ));

                }
            });
        } else {
            System.out.println("Request failed with code: " + response.code());

        }
        return monthRank;
    }

    public static List<PiaotianClassification> getSearch(String keyword, String classification,int Page) throws IOException {
        String url = "https://www.piaotia.com/modules/article/search.php?searchtype=CLASS&searchkey=KEYWORD&Submit=+%CB%D1+%CB%F7+&page=INDEX";
        String encoded = URLEncoder.encode(keyword, "GBK");
        url = url.replace("KEYWORD",encoded);
        url = url.replace("CLASS",classification);
        url = url.replace("INDEX",String.valueOf(Page));
        List<PiaotianClassification> searchList = new ArrayList<>();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            document = Jsoup.parse(response.body().byteStream(), "GBK", url);

            Elements urlList = document.select("table.grid tr");

            if(document.select("div.pagelink a.last").text().equals("")) {
                if(Page != 1) {
                    return searchList;
                }
                Elements elements = document.select("table tr td");
                searchList.add(new PiaotianClassification(
                        response.request().url().toString(),
                        TranslateUtil.chs2cht(elements.get(1).text()),
                        TranslateUtil.chs2cht(elements.get(4).text().split("：")[1])
                ));

                return searchList;
            }

            int MaxPage = Integer.parseInt(document.select("div.pagelink a.last").text());
            if(Page >= MaxPage + 1){
                return searchList;
            }
            urlList.forEach(element -> {
                Elements bookInfo = element.select("td");
                if (bookInfo.size() > 1) {
                    //bookName
                    //System.out.println(bookInfo.get(0).text());
                    //bookInfoUrl
                    //System.out.println("https://www.piaotia.com" + bookInfo.get(0).select("a").attr("href"));
                    //bookAuthor
                    //System.out.println(bookInfo.get(2).text());
                    searchList.add(new PiaotianClassification(
                            bookInfo.get(0).select("a").attr("href"),
                            TranslateUtil.chs2cht(bookInfo.get(0).text()),
                            TranslateUtil.chs2cht(bookInfo.get(2).text())
                    ));

                }
            });
        } else {
            System.out.println("Request failed with code: " + response.code());
        }

        return searchList;
    }

    public static List<PiaotianClassification> getBookListByUrl(String url,int Page) throws IOException {
        List<PiaotianClassification> bookList = new ArrayList<>();
        String URL = "";
        if(url.contains("quanben")) {
            URL = url + "?page=" + Page;
        }
        else {
            URL = url.replace("1.html", Page + ".html");
        }

        Request request = new Request.Builder()
                .url(URL)
                .build();
        Document document = null;
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()){
            document = Jsoup.parse(response.body().byteStream(), "GBK", url);

            Elements urlList = document.select("table.grid tr");
            int MaxPage = Integer.parseInt(document.select("div.pagelink a.last").text());
            if(Page >= MaxPage + 1){
                return bookList;
            }
            String header = "https://www.piaotia.com/";

            urlList.forEach(element -> {
                Elements bookInfo = element.select("td");
                if (bookInfo.size() > 1) {
                    bookList.add(new PiaotianClassification(
                            bookInfo.get(0).select("a").attr("href").startsWith(header) ? bookInfo.get(0).select("a").attr("href") : header +  bookInfo.get(0).select("a").attr("href"),
                            TranslateUtil.chs2cht(bookInfo.get(0).text()),
                            TranslateUtil.chs2cht(bookInfo.get(2).text())
                    ));
                }
            });

        } else {
            System.out.println("Request failed with code: " + response.code());
        }

        return  bookList;
    }

    public static PiaotianBookDetail getBookDetail(String url) throws IOException {
        PiaotianBookDetail book = new PiaotianBookDetail();
        if(url.startsWith("CHAPTER:")) {
            url = getBookInfoByChapter(url.replace("CHAPTER:",""));
        }
        System.out.println(url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()){
            document = Jsoup.parse(response.body().byteStream(), "GBK", url);
            Elements elements = document.select("table tr td");

            String bookName = TranslateUtil.chs2cht(elements.get(1).text());
            book.setName(bookName);
            book.setAuthor(TranslateUtil.chs2cht(elements.get(4).text()));
            book.setDesc(TranslateUtil.chs2cht(elements.get(19).text().split("内容简介：")[1]));
            book.setImageURL(elements.stream().filter(
                            element -> element.select("a").attr("href").endsWith(".jpg"))
                    .findAny().get().select("a").attr("href"));

            String tempUrl = elements.get(17).select("a").attr("href");
            String basicURL;
            if(tempUrl.startsWith("/html")) {
                basicURL = "https://www.piaotia.com" + tempUrl;
            }
            else {
                basicURL = tempUrl;
            }
            request = new Request.Builder()
                    .url(basicURL)
                    .build();
            document = null;
            response = client.newCall(request).execute();

            if(response.isSuccessful()){
                document = Jsoup.connect(basicURL).get();
                elements = document.select("div.centent ul li");


                List<String> name = elements.stream()
                        .map(element -> TranslateUtil.chs2cht(element.select("a").text()))
                        .filter(text -> !text.equals(""))
                        .collect(Collectors.toList());

                List<String> html = elements.stream()
                        .map(element -> basicURL + element.select("a").attr("href"))
                        .filter(text -> text.endsWith(".html"))
                        .collect(Collectors.toList());

                book.setChapterHTML(html);
                book.setChapterName(name);

            }
            else {
                System.out.println("Request failed with code: " + response.code());
            }
        }
        else {
            System.out.println("Request failed with code: " + response.code());
        }

        return book;
    }

    public static String[] getChapter(String url) throws IOException {
        String []book = new String[2];

        Request request = new Request.Builder()
                .url(url)
                .build();

        Document document = null;
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()){
            document = Jsoup.parse(response.body().byteStream(), "GBK", url);
            String content = document.body().html();
            String[] list = content.split("<br>");

            book[0] = list[0].split("</h1>")[0].split("</a>")[15];
            book[1] = "";
            for(int i=0;i<list.length;i++) {
                if(list[i].startsWith("&nbsp;") || list[i].startsWith(" &nbsp;")) {
                    book[1] = book[1] + list[i].replace("&nbsp;&nbsp;&nbsp;&nbsp;", "\t").replace("\n", "").replace("&#x2003;&#x2003;", " ").split("<!-")[0] + "\n\n";
                }
            }
            book[0] = TranslateUtil.chs2cht(book[0]);
            book[1] = TranslateUtil.chs2cht(book[1]);

        }
        else {
            System.out.println("Request failed with code: " + response.code());
            System.out.println(response.header("Retry-After"));
            if(response.header("Retry-After") != null || !Objects.equals(response.header("Retry-After"), "0")) {
                retrySec = Integer.parseInt(response.header("Retry-After"));
            }
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
            document = Jsoup.parse(response.body().byteStream(), "GBK", url);
        }
        else {
            System.out.println("Request failed with code: " + response.code());
        }

        System.out.println(document.select("a").get(1).attr("href"));
        return document.select("a").get(1).attr("href");
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
                values.put("website", "Piaotian");
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
                builder.setOngoing(false);
                throw new Exception("預載失敗");
        }
    }
}