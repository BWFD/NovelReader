package com.example.novelreader.service;

import com.example.novelreader.dao.hjwzwBookDetail;
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
        int maxPage = 1 ;
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
        }
        Elements elements = document.select("h1");
        book[0] = elements.text();

        elements = document.select("table tbody tr td div[style=font-size: 20px; line-height: 30px; word-wrap: break-word; table-layout: fixed; word-break: break-all; width: 750px; margin: 0 auto; text-indent: 2em;]");
        String html = elements.get(0).html();
        String temp[] = html.split("</p>");
        html = "";
        for (int i = 2; i < temp.length-2; i++) {
            //System.out.println(book[i]);
            html = html + temp[i].replace("<p>","\n").replace("<br>","");
        }
        book[1] = html;
        return book;
    }
}
