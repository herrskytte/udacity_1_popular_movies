package no.skytte.popularmovies.models;

import java.io.Serializable;

//TODO Consider Parcelable/bundle intead of Serializable
public class Trailer implements Serializable{

    private String id;
    private String key;
    private String name;
    private String site;

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }
}
