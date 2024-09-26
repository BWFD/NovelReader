package com.example.novelreader.dao;

import java.util.ArrayList;
import java.util.List;

public class CZBooksBookDetail {

    private String name;
    private String author;
    private String desc;
    private String imageURL;
    private List<String> chapterName = new ArrayList<>();
    private List<String> chapterHTML = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public List<String> getChapterName() {
        return chapterName;
    }

    public void setChapterName(List<String> chapterName) {
        this.chapterName = chapterName;
    }

    public List<String> getChapterHTML() {
        return chapterHTML;
    }

    public void setChapterHTML(List<String> chapterHTML) {
        this.chapterHTML = chapterHTML;
    }
}
