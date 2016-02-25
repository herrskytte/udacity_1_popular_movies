package no.skytte.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import no.skytte.popularmovies.models.Movie;

public class MovieAdapter extends BaseAdapter {

    List<Movie> mData = new ArrayList<>();
    Context mContext;
    LayoutInflater mInflater;
    String mPosterSize = "w185";

    public MovieAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view == null){
            view = mInflater.inflate(R.layout.movie_list_content, null);
        }
        ImageView iv = (ImageView) view.findViewById(R.id.posterimage);
        String path = createPosterPath(mData.get(position));
        Picasso.with(mContext).load(path).into(iv);
        return view;
    }

    private String createPosterPath(Movie movie) {
        return "http://image.tmdb.org/t/p/" + mPosterSize + movie.getPosterPath();
    }

    public void setData(List<Movie> movies) {
        mData = movies;
        notifyDataSetChanged();
    }
}
