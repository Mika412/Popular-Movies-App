package com.example.android.popularmovies.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.MainActivity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Utilities.Constants;
import com.squareup.picasso.Picasso;

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder>{

    final private ListItemClickListener mOnClickListener;

    private Cursor mCursor;

    public PosterAdapter(ListItemClickListener clickListener) {
        this.mOnClickListener = clickListener;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutViewId = R.layout.list_item;
        View item = inflater.inflate(layoutViewId, parent, false);
        return new PosterViewHolder(item);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        Picasso.with(holder.iv_poster.getContext()).load(Constants.POSTER_BASE_URL + mCursor.getString(MainActivity.INDEX_POSTER_URL)).into(holder.iv_poster);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public interface ListItemClickListener{
        void onClick(int clickedMovie);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView iv_poster;

        public PosterViewHolder(View itemView) {
            super(itemView);
            iv_poster = (ImageView) itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            mOnClickListener.onClick(mCursor.getInt(0));
        }
    }
}
