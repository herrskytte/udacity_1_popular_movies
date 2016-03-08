package no.skytte.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.popularmovies.models.Movie;
import no.skytte.popularmovies.models.MovieResult;
import no.skytte.popularmovies.moviedb.RestClient;
import no.skytte.popularmovies.provider.MoviesContract;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String SORT_POPULAR = "popularity.desc";
    private static final String SORT_RATED = "vote_average.desc";
    private static final String SORT_FAVORITE = "favorite";

    private static final String SAVED_INSTANCE_SORT = "mCurrentSortOrder";
    private static final String SAVED_INSTANCE_MOVIES = "mMovieList";

    private boolean mTwoPane;
    private String mCurrentSortOrder = SORT_POPULAR;

    @Bind(R.id.movie_list) GridView gridView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.empty_list_view) TextView emptyView;


    ArrayList<Movie> mMovieList = new ArrayList<>();
    MovieAdapter mAdapter;
    RestClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mClient = new RestClient();

        mAdapter = new MovieAdapter(this);
        gridView.setAdapter(mAdapter);
        gridView.setEmptyView(emptyView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = (Movie) mAdapter.getItem(position);
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(MovieDetailFragment.ARG_MOVIE, selectedMovie);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Intent intent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
                    intent.putExtra(MovieDetailFragment.ARG_MOVIE, selectedMovie);
                    MovieListActivity.this.startActivity(intent);
                }
            }
        });

        if (findViewById(R.id.movie_detail_container) != null) {
            // If detail container is present, the activity should be in two-pane mode.
            mTwoPane = true;
        }

        if(savedInstanceState != null){
            mCurrentSortOrder = savedInstanceState.getString(SAVED_INSTANCE_SORT);
            mMovieList = savedInstanceState.getParcelableArrayList(SAVED_INSTANCE_MOVIES);
            mAdapter.setData(mMovieList);
        }
        else{
            updateMovies();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_INSTANCE_SORT, mCurrentSortOrder);
        outState.putParcelableArrayList(SAVED_INSTANCE_MOVIES, mMovieList);
    }

    private void updateMovies() {
        if(mCurrentSortOrder.equals(SORT_FAVORITE)){
            getSupportLoaderManager().restartLoader(0, null, this);
        }
        else {
            Call<MovieResult> call = mClient.getMovieDbService().getMovieList(mCurrentSortOrder);
            call.enqueue(new Callback<MovieResult>() {
                @Override
                public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                    Log.i("MainActivity", "Result ok!");
                    mMovieList = response.body().getResults();
                    mAdapter.setData(mMovieList);
                }

                @Override
                public void onFailure(Call<MovieResult> call, Throwable t) {
                    Log.e("MainActivity", "No results!", t);
                    mMovieList = new ArrayList<>();
                    mAdapter.setData(mMovieList);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String currentSortOrder = mCurrentSortOrder;
        switch (item.getItemId()){
            case R.id.action_sort_popular :
                mCurrentSortOrder = SORT_POPULAR;
                break;
            case R.id.action_sort_rated :
                mCurrentSortOrder = SORT_RATED;
                break;
            case R.id.action_sort_favorite :
                mCurrentSortOrder = SORT_FAVORITE;
                break;
        }
        if(!mCurrentSortOrder.equals(currentSortOrder)){
            updateMovies();
        }
        return super.onOptionsItemSelected(item);
    }

    static final String[] PROJECTION = new String[] {
            MoviesContract.Movies._ID,
            MoviesContract.Movies.MOVIE_ID,
            MoviesContract.Movies.MOVIE_TITLE,
            MoviesContract.Movies.MOVIE_OVERVIEW,
            MoviesContract.Movies.MOVIE_POSTER,
            MoviesContract.Movies.MOVIE_RELEASEDATE,
            MoviesContract.Movies.MOVIE_VOTEAVG,
            MoviesContract.Movies.MOVIE_VOTECOUNT
    };

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri baseUri = MoviesContract.Movies.CONTENT_URI;
        return new CursorLoader(this, baseUri,
                PROJECTION, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i("MainActivity", "Loader finished ok! Items: " + data.getCount());
        mMovieList = new ArrayList<>();
        while(data.moveToNext()){
            mMovieList.add(new Movie(data));
        }
        mAdapter.setData(mMovieList);
    }

    public void onLoaderReset(Loader<Cursor> loader) {}
}
