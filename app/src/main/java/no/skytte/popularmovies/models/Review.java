package no.skytte.popularmovies.models;

import java.io.Serializable;

//TODO Consider Parcelable/bundle intead of Serializable
public class Review implements Serializable{

    private String id;
    private String author;
    private String content;

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
