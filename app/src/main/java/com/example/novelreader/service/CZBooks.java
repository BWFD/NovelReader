package com.example.novelreader.service;

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
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CZBooks {

    private static OkHttpClient client = new OkHttpClient();

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
            Elements elements = document.select("ul.nav.novel-list div.novel-item");
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
               book[1] = book[1] + str.replace("  ","").replace("　　","");

            });
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
            document = Jsoup.parse(response.body().byteStream(), "UTF-8", url);
        }
        else {
            System.out.println("Request failed with code: " + response.code());
        }
        return "https:" + document.select("div.position a").get(2).attr("href");
    }
}
