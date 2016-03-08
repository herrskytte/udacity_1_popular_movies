package no.skytte.popularmovies.provider;

public enum MoviesUriEnum {
    MOVIES(100, "movies", MoviesContract.Movies.CONTENT_TYPE_ID, false, MovieDatabase.Tables.MOVIES),
    MOVIES_ID(102, "movies/*", MoviesContract.Movies.CONTENT_TYPE_ID, true, null),
    TRAILERS(200, "trailers", MoviesContract.Trailers.CONTENT_TYPE_ID, false, MovieDatabase.Tables.TRAILERS),
    TRAILERS_ID(201, "trailers/*", MoviesContract.Trailers.CONTENT_TYPE_ID, false, null),
    REVIEWS(300, "reviews", MoviesContract.Reviews.CONTENT_TYPE_ID, false, MovieDatabase.Tables.REVIEWS),
    REVIEWS_ID(301, "reviews/*", MoviesContract.Reviews.CONTENT_TYPE_ID, true, null);

    public int code;

    public String path;

    public String contentType;

    public String table;

    MoviesUriEnum(int code, String path, String contentTypeId, boolean item, String table) {
        this.code = code;
        this.path = path;
        this.contentType = item ? MoviesContract.makeContentItemType(contentTypeId)
                : MoviesContract.makeContentType(contentTypeId);
        this.table = table;
    }


}
