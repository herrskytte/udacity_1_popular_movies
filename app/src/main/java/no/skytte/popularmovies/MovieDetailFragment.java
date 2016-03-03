package no.skytte.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.popularmovies.models.Movie;
import no.skytte.popularmovies.models.SearchResult;
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

    @Bind(R.id.movie_detail) TextView mDetailsText;
    @Bind(R.id.release_date) TextView mReleaseText;
    @Bind(R.id.votes) TextView mRatingText;
    @Bind(R.id.trailer_list) GridView gridView;

    MovieAdapter mAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            mCurrentMovie = (Movie) getArguments().getSerializable(ARG_MOVIE);
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

        mAdapter = new MovieAdapter(getActivity());
        gridView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMovies();
    }

    private void updateMovies() {
        Call<SearchResult> call = new RestClient().getMovieDbService().getMovieList("popularity.desc");
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                Log.i("MainActivity", "Result ok!");
                mAdapter.setData(response.body().getResults());
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Log.e("MainActivity", "No results!", t);
                mAdapter.setData(new ArrayList<Movie>());
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
