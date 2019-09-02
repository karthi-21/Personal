package com.lucifer.personal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LiveTvAdapter extends RecyclerView.Adapter<LiveTvAdapter.TvViewHolder> {

    private Context mContext;
    private ArrayList<LiveTv> mTvList;
    private LiveTvAdapter.OnItemClickListener mListener;
    private LiveTvAdapter.OnItemLongClickListener mLongListener;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    String type = "";
    String dubbed = "";

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(LiveTvAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnItemLongClickListener(LiveTvAdapter.OnItemLongClickListener listener) {
        mLongListener = listener;
    }

    public LiveTvAdapter(Context context, ArrayList<LiveTv> tvList) {
        mContext = context;
        mTvList = tvList;
    }

    @Override
    public TvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.live_tv_item, parent, false);
        return new TvViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TvViewHolder holder, int position) {
        final LiveTv tvItem = mTvList.get(position);

        holder.mTextView.setText(tvItem.name);
        Picasso.get().load(tvItem.getImgUrl()).fit().into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mTvList.size();
    }

    public class TvViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;

        public TvViewHolder(View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            mImageView = itemView.findViewById(R.id.newsIcon);
            mTextView = itemView.findViewById(R.id.newsName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mLongListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mLongListener.onItemLongClick(position);
                        }
                    }
                    return true;
                }
            });

        }
    }
}