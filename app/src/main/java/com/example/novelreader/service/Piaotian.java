package com.example.novelreader.service;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.novelreader.dao.PiaotianBookDetail;
import com.example.novelreader.dao.PiaotianClassification;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Piaotian {

    private static OkHttpClient client = new OkHttpClient();


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
                        "https://www.piaotia.com" + element.attr("href"), Translate.chs2cht(element.text()), "NULL"));
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
                            "https://www.piaotia.com" + bookInfo.get(0).select("a").attr("href"),
                            Translate.chs2cht(bookInfo.get(0).text()),
                            Translate.chs2cht(bookInfo.get(2).text())
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
                        Translate.chs2cht(elements.get(1).text()),
                        Translate.chs2cht(elements.get(4).text().split("：")[1])
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
                            Translate.chs2cht(bookInfo.get(0).text()),
                            Translate.chs2cht(bookInfo.get(2).text())
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
        String URL = url.replace("1.html",Page + ".html");

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
                            Translate.chs2cht(bookInfo.get(0).text()),
                            Translate.chs2cht(bookInfo.get(2).text())
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

            String bookName = Translate.chs2cht(elements.get(1).text());
            book.setName(bookName);
            book.setAuthor(Translate.chs2cht(elements.get(4).text()));
            book.setDesc(Translate.chs2cht(elements.get(19).text().split("内容简介：")[1]));
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
                        .map(element -> Translate.chs2cht(element.select("a").text()))
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
            book[0] = Translate.chs2cht(book[0]);
            book[1] = Translate.chs2cht(book[1]);

        }
        else {
            System.out.println("Request failed with code: " + response.code());
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

        return document.select("a").get(1).attr("href");
    }
}