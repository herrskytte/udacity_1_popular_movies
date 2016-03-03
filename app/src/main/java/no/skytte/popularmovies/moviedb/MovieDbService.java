package no.skytte.popularmovies.moviedb;

import no.skytte.popularmovies.models.SearchResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDbService {
    String APIKEY = "d9987f6d38c9c183d75323198a12406c";

    @GET("discover/movie?api_key=" + APIKEY +"&vote_count.gte=50")
    Call<SearchResult> getMovieList(@Query("sort_by") String sort);

}
