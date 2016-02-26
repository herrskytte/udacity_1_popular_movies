package no.skytte.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.popularmovies.models.Movie;
import no.skytte.popularmovies.models.SearchResult;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    private static final String SORT_POPULAR = "popularity.desc";
    private static final String SORT_RATED = "vote_average.desc";

    private boolean mTwoPane;
    private String mCurrentSortOrder = SORT_POPULAR;

    @Bind(R.id.movie_list) GridView gridView;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.empty_list_view) TextView emptyView;

    MovieAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        mAdapter = new MovieAdapter(this);
        gridView.setAdapter(mAdapter);
        gridView.setEmptyView(emptyView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie selectedMovie = (Movie) mAdapter.getItem(position);
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putSerializable(MovieDetailFragment.ARG_MOVIE, selectedMovie);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO Cache data in saveinstancestate to avoid network calls. Only execute when empty
        new MovieListDownloader().execute();
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
        }
        if(!mCurrentSortOrder.equals(currentSortOrder)){
            new MovieListDownloader().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    private class MovieListDownloader extends AsyncTask<Void, Void, SearchResult>{

        private String APIKEY = "input_key_here";
        private String BASE_URL = "http://api.themoviedb.org/3/discover/movie";

        @Override
        protected SearchResult doInBackground(Void... params) {
            try {
                String url = Uri.parse(BASE_URL)
                        .buildUpon()
                        .appendQueryParameter("api_key", APIKEY)
                        .appendQueryParameter("vote_count.gte", "50")
                        .appendQueryParameter("sort_by", mCurrentSortOrder)
                        .build()
                        .toString();
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
                return restTemplate.getForObject(url, SearchResult.class);

            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(SearchResult movies) {
            if(movies != null){
                Log.i("MainActivity", "Result ok!");
                mAdapter.setData(movies.getResults());
            }
            else{
                Log.i("MainActivity", "No results!");
                mAdapter.setData(new ArrayList<Movie>());
            }
        }

    }
}
