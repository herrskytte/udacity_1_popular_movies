package no.skytte.popularmovies;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import no.skytte.popularmovies.models.Review;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> mDataset = new ArrayList<>();
    private Activity mContext;

    public ReviewsAdapter(Activity context) {
        mContext = context;
    }

    public void updateDataset(List<Review> myDataset){
        mDataset = myDataset;
        notifyDataSetChanged();
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Review review = mDataset.get(position);
        holder.author.setText(review.getAuthor());
        holder.review.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        @Bind(R.id.author) TextView author;
        @Bind(R.id.review) TextView review;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            view = v;
        }
    }
}
