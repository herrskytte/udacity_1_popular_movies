package no.skytte.popularmovies;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import no.skytte.popularmovies.models.Trailer;

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private List<Trailer> mDataset = new ArrayList<>();
    private Activity mContext;

    public TrailersAdapter(Activity context) {
        mContext = context;
    }

    public void updateDataset(List<Trailer> myDataset){
        mDataset = myDataset;
        notifyDataSetChanged();
    }

    @Override
    public TrailersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_trailer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Trailer trailer = mDataset.get(position);
//        holder.source.setText(trailer.getSource().getDescriptionStringRef());
//        holder.name.setText(mContext.getString(R.string.search_fullname, trailer.getFirstName(), trailer.getLastName()));
//        holder.si.setVisibility(trailer.isSiFlag() ? View.VISIBLE : View.GONE);
//        holder.mi.setVisibility(trailer.isMiFlag() ? View.VISIBLE : View.GONE);
//        if (TextUtils.isEmpty(trailer.getAddress())) {
//            holder.address.setVisibility(View.GONE);
//        } else {
//            holder.address.setVisibility(View.VISIBLE);
//            holder.address.setText(trailer.getAddress());
//        }
//        holder.post.setText(createPostAddress(trailer));
//
//        holder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                removeKeyboard();
//                Intent intent = mContext.getIntent();
//                intent.putExtra(AppConstants.PERSONALIA_EXTRA, trailer.createBundle());
//                mContext.setResult(Activity.RESULT_OK, intent);
//                mContext.finish();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
//        @Bind(R.id.source_label)
//        TextView source;
//        @Bind(R.id.si_label) TextView si;
//        @Bind(R.id.mi_label) TextView mi;
//        @Bind(R.id.name_text) TextView name;
//        @Bind(R.id.address_text) TextView address;
//        @Bind(R.id.post_text) TextView post;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            view = v;
        }
    }
}


//        extends BaseAdapter {
//
//    List<Trailer> mData = new ArrayList<>();
//    Context mContext;
//    LayoutInflater mInflater;
//    String mPosterSize = "w185";
//
//    public TrailersAdapter(Context context){
//        mContext = context;
//        mInflater = LayoutInflater.from(mContext);
//    }
//
//    @Override
//    public int getCount() {
//        return mData.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mData.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View view, ViewGroup parent) {
//        if(view == null){
//            view = mInflater.inflate(R.layout.list_item_trailer, null);
//        }
//        return view;
//    }
//
//
//    public void setData(List<Trailer> trailers) {
//        mData = trailers;
//        notifyDataSetChanged();
//    }
//}
