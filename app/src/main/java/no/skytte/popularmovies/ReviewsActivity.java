package no.skytte.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.popularmovies.models.Movie;
import no.skytte.popularmovies.models.Review;
import no.skytte.popularmovies.models.ReviewsResult;
import no.skytte.popularmovies.moviedb.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity {

    public static final String ARG_MOVIE = "movie_intent_extra";

    @Bind(R.id.reviews_list) RecyclerView mReviewsList;
    @Bind(R.id.toolbar) Toolbar toolbar;

    Movie mCurrentMovie;
    ReviewsAdapter mAdapter;

    RestClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        ButterKnife.bind(this);

        mCurrentMovie = getIntent().getParcelableExtra(ARG_MOVIE);
        if(mCurrentMovie == null){
            finish();
            return;
        }

        toolbar.setTitle("Reviews for: " + mCurrentMovie.getTitle());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClient = new RestClient();

        mReviewsList.setHasFixedSize(true);

        int columns =  getResources().getConfiguration().screenWidthDp / 250;
        Log.i("ReviewsActivity", "Review columns: " + columns + " for screenWidth: " + getResources().getConfiguration().screenWidthDp);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        mReviewsList.setLayoutManager(staggeredGridLayoutManager);

        mAdapter = new ReviewsAdapter(this);
        mReviewsList.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO Cache data in saveinstancestate to avoid network calls. Only execute when empty
//        new MovieListDownloader().execute();
        updateReviews();
    }

    private void updateReviews() {
        Call<ReviewsResult> call = mClient.getMovieDbService().getReviews(mCurrentMovie.getId());
        call.enqueue(new Callback<ReviewsResult>() {
            @Override
            public void onResponse(Call<ReviewsResult> call, Response<ReviewsResult> response) {
                Log.i("ReviewsActivity", "Result ok!");
                mAdapter.updateDataset(response.body().getResults());
            }

            @Override
            public void onFailure(Call<ReviewsResult> call, Throwable t) {
                Log.e("ReviewsActivity", "No results!", t);
                mAdapter.updateDataset(new ArrayList<Review>());
            }
        });
    }
}