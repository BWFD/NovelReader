package com.example.novelreader.dao;

public class hjwzwClassification {

    private String name;
    private String url;
    private String image;
    private String author;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public hjwzwClassification(String name, String url, String image, String author) {
        this.name = name;
        this.url =  url;
        this.image = image;
        this.author = author;
    }
}
