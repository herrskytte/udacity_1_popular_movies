package no.skytte.popularmovies.moviedb;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    private static final String apiKey = "YOUR-API-KEY-HERE";
    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private MovieDbService movieDbService;

    public RestClient()
    {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
//        .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")

        Retrofit retrofitClient = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        movieDbService = retrofitClient.create(MovieDbService.class);
    }

    public MovieDbService getMovieDbService()
    {
        return movieDbService;
    }
}
