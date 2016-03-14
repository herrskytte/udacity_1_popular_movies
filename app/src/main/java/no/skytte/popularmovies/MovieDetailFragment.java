package no.skytte.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import no.skytte.popularmovies.provider.MoviesContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String ARG_MOVIE = "movie";

    Movie mCurrentMovie;
    boolean mIsFavoriteMovie = false;

    @Bind(R.id.details_root) LinearLayout mRootLayout;
    @Bind(R.id.movie_detail) TextView mDetailsText;
    @Bind(R.id.release_date) TextView mReleaseText;
    @Bind(R.id.trailers_title) TextView mTrailersTitle;
    @Bind(R.id.votes) TextView mRatingText;
    @Bind(R.id.favorite_btn) Button mFavoriteBtn;

    List<Trailer> mTrailers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_MOVIE)) {
            mCurrentMovie = getArguments().getParcelable(ARG_MOVIE);
        }

        setRetainInstance(true);

        if(mTrailers == null){
            getTrailers();
        }

        setHasOptionsMenu(true);
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

        getLoaderManager().initLoader(0, null, this);

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
        if(getActivity() == null){
            return;
        }
        if(mTrailers.isEmpty()){
            mTrailersTitle.setVisibility(View.GONE);
        }
        else{
            mTrailersTitle.setVisibility(View.VISIBLE);
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
                    }
                });
                mRootLayout.addView(v);
            }
        }
    }

    @OnClick(R.id.reviews_btn)
    public void clickSeeReviews(){
        Intent intent = new Intent(getActivity(), ReviewsActivity.class);
        intent.putExtra(ReviewsActivity.ARG_MOVIE, mCurrentMovie);
        startActivity(intent);
    }

    @OnClick(R.id.favorite_btn)
    public void toggleFavorite(){
        updateFavoriteButton(!mIsFavoriteMovie);
        if(mIsFavoriteMovie){
            getActivity().getContentResolver().insert(MoviesContract.Movies.CONTENT_URI, mCurrentMovie.getContentValues());
        }
        else{
            getActivity().getContentResolver().delete(MoviesContract.Movies.buildUri(mCurrentMovie.getId()), null, null);
        }
    }

    private void updateFavoriteButton(boolean isFavorite) {
        mIsFavoriteMovie = isFavorite;
        mFavoriteBtn.setText(mIsFavoriteMovie ? R.string.favorite_undo_text : R.string.favorite_text);
        mFavoriteBtn.setCompoundDrawablesWithIntrinsicBounds(mIsFavoriteMovie ? R.drawable.ic_star : R.drawable.ic_star_border, 0, 0, 0);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    static final String[] PROJECTION = new String[] {
            MoviesContract.Movies._ID
    };

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = MoviesContract.Movies.buildUri(mCurrentMovie.getId());
        return new CursorLoader(getActivity(), baseUri,
                PROJECTION, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateFavoriteButton(data.getCount() > 0);
    }

    public void onLoaderReset(Loader<Cursor> loader) {}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.share, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_share){
            if(mTrailers == null || mTrailers.isEmpty()){
                Snackbar.make(mRootLayout, R.string.no_trailers, Snackbar.LENGTH_SHORT).show();
            }
            else{
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, createSharedText(mTrailers.get(0)));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String createSharedText(Trailer trailer) {
        return "Check out the trailer for " + mCurrentMovie.getTitle() + ":\n" +
        "https://www.youtube.com/watch?v=" + trailer.getKey();
    }
}
