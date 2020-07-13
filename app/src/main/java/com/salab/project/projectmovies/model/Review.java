package com.salab.project.projectmovies.model;
/**
 * Model class for reviews
 */
public class Review {

    private String id;
    private String author;
    private String content;
    private String url;

    public Review(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

}
