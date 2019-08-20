package com.lucifer.personal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Context mContext;
    private ArrayList<Movie> mMovieList;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    String type = "";
    String dubbed = "";

    public interface OnItemClickListener {
        void onItemClick(int position, String type, String dubbed);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongListener = listener;
    }

    public MoviesAdapter(Context context, ArrayList<Movie> movieList) {
        mContext = context;
        mMovieList = movieList;
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.movie_card, parent, false);
        return new MoviesAdapter.MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MoviesAdapter.MovieViewHolder holder, int position) {
        final Movie movieItem = mMovieList.get(position);
        type = movieItem.type;
        dubbed = movieItem.dubbed;

        Picasso.get().load(movieItem.getImgUrl()).fit().into(holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            mImageView = itemView.findViewById(R.id.movieCardImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position, type, dubbed);
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