package com.example.novelreader.service;

import com.example.novelreader.dao.hjwzwClassification;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class hjwzw {
    private static OkHttpClient client = new OkHttpClient();

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
            if(e.text().equals("首 頁") || e.text().equals("手機版") || e.text().equals("移動版") || e.text().equals("書架")) {
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
        int maxPage = Integer.parseInt(elements.get(elements.size()-3).text());
        if(page > maxPage) {
            return null;
        }
        elements = document.select("tbody tr td table tbody tr td");
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
}
