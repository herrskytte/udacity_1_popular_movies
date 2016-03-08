package no.skytte.popularmovies.moviedb;

import no.skytte.popularmovies.models.MovieResult;
import no.skytte.popularmovies.models.ReviewsResult;
import no.skytte.popularmovies.models.TrailersResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbService {
    String APIKEY = "insertkeyhere";

    @GET("discover/movie?api_key=" + APIKEY +"&vote_count.gte=50")
    Call<MovieResult> getMovieList(@Query("sort_by") String sort);

    @GET("movie/{id}/videos?api_key=" + APIKEY)
    Call<TrailersResult> getTrailers(@Path("id") String id);

    @GET("movie/{id}/reviews?api_key=" + APIKEY)
    Call<ReviewsResult> getReviews(@Path("id") String id);
}
