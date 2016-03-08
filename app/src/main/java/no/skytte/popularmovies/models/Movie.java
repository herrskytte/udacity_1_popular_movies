package no.skytte.popularmovies.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import no.skytte.popularmovies.provider.MoviesContract.Movies;

public class Movie implements Parcelable{

    private String id;
    private String title;
    private String overview;
    private String posterPath;
    private String releaseDate;
    private String voteAverage;
    private String voteCount;

    public Movie(Cursor data) {
        id = data.getString(data.getColumnIndex(Movies.MOVIE_ID));
        title = data.getString(data.getColumnIndex(Movies.MOVIE_TITLE));
        overview = data.getString(data.getColumnIndex(Movies.MOVIE_OVERVIEW));
        posterPath = data.getString(data.getColumnIndex(Movies.MOVIE_POSTER));
        releaseDate = data.getString(data.getColumnIndex(Movies.MOVIE_RELEASEDATE));
        voteAverage = data.getString(data.getColumnIndex(Movies.MOVIE_VOTEAVG));
        voteCount = data.getString(data.getColumnIndex(Movies.MOVIE_VOTECOUNT));
    }

    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        overview = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readString();
        voteCount = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeString(voteAverage);
        dest.writeString(voteCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(Movies.MOVIE_ID, id);
        cv.put(Movies.MOVIE_TITLE, title);
        cv.put(Movies.MOVIE_OVERVIEW, overview);
        cv.put(Movies.MOVIE_POSTER, posterPath);
        cv.put(Movies.MOVIE_RELEASEDATE, releaseDate);
        cv.put(Movies.MOVIE_VOTEAVG, voteAverage);
        cv.put(Movies.MOVIE_VOTECOUNT, voteCount);
        return cv;
    }
}
