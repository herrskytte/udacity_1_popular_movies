package no.skytte.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    @Bind(R.id.trailer_list) RecyclerView mTrailersList;

    TrailersAdapter mAdapter;


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

        mTrailersList.setNestedScrollingEnabled(false);
        mTrailersList.setHasFixedSize(false);
        mAdapter = new TrailersAdapter(getActivity());
        mTrailersList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        getTrailers();
    }

    private void getTrailers() {
        Call<TrailersResult> call = new RestClient().getMovieDbService().getTrailers(mCurrentMovie.getId());
        call.enqueue(new Callback<TrailersResult>() {
            @Override
            public void onResponse(Call<TrailersResult> call, Response<TrailersResult> response) {
                Log.i("MovieDetailFragment", "Result ok!");
                List<Trailer> trailersList = response.body().getResults();
//                for(int i= 0; i < 100; i++){
//                    trailersList.add(new Trailer());
//                }
                mAdapter.updateDataset(response.body().getResults());

                for (final Trailer t : trailersList) {
                    View v = LayoutInflater.from(getContext())
                            .inflate(R.layout.list_item_trailer, mRootLayout, false);
                    TextView tv = ButterKnife.findById(v, R.id.trailer_name);
                    tv.setText(t.getName());
                    v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Clicked" + t.getId(), Toast.LENGTH_LONG).show();
                        }
                    });
                    mRootLayout.addView(v);
                }
            }

            @Override
            public void onFailure(Call<TrailersResult> call, Throwable t) {
                Log.e("MovieDetailFragment", "No results!", t);
                mAdapter.updateDataset(new ArrayList<Trailer>());
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
