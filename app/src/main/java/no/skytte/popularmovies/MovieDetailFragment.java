package no.skytte.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import no.skytte.popularmovies.models.Movie;
import no.skytte.popularmovies.models.Trailer;
import no.skytte.popularmovies.models.TrailersResult;
import no.skytte.popularmovies.moviedb.RestClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {

    public static final String ARG_MOVIE = "movie";

    Movie mCurrentMovie;

    @Bind(R.id.details_root) LinearLayout mRootLayout;
    @Bind(R.id.movie_detail) TextView mDetailsText;
    @Bind(R.id.release_date) TextView mReleaseText;
    @Bind(R.id.votes) TextView mRatingText;

    List<Trailer> mTrailers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            mCurrentMovie = (Movie) getArguments().getSerializable(ARG_MOVIE);
        }

        setRetainInstance(true);

        if(mTrailers == null){
            getTrailers();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        mDetailsText.setText(mCurrentMovie.getOverview());
        mReleaseText.setText(mCurrentMovie.getReleaseDate());
        mRatingText.setText(getString(R.string.rating, mCurrentMovie.getVoteAverage(), mCurrentMovie.getVoteCount()));


        if(mTrailers != null){
            updateTrailerViews();
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void getTrailers() {
        Call<TrailersResult> call = new RestClient().getMovieDbService().getTrailers(mCurrentMovie.getId());
        call.enqueue(new Callback<TrailersResult>() {
            @Override
            public void onResponse(Call<TrailersResult> call, Response<TrailersResult> response) {
                Log.i("MovieDetailFragment", "Result ok!");
                mTrailers = response.body().getResults();
                updateTrailerViews();
            }

            @Override
            public void onFailure(Call<TrailersResult> call, Throwable t) {
                Log.e("MovieDetailFragment", "No results!", t);
            }
        });
    }

    private void updateTrailerViews() {
        for (final Trailer t : mTrailers) {
            View v = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item_trailer, mRootLayout, false);
            TextView tv = ButterKnife.findById(v, R.id.trailer_name);
            tv.setText(t.getName());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + t.getKey()));
                    startActivity(intent);
                    //Toast.makeText(getActivity(), "Clicked" + t.getId(), Toast.LENGTH_LONG).show();
                }
            });
            mRootLayout.addView(v);
        }
    }

    @OnClick(R.id.reviews_btn)
    public void clickSeeReviews(){
        Intent intent = new Intent(getActivity(), ReviewsActivity.class);
        intent.putExtra(ReviewsActivity.ARG_MOVIE, mCurrentMovie);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
