package com.example.novelreader.dao;

public class PiaotianClassification {
    private String html;
    private String name;
    private String author;

    public PiaotianClassification(String html, String name,String author) {
        this.html = html;
        this.name = name;
        this.author = author;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

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
}
