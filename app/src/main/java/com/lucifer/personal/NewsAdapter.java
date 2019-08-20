package com.lucifer.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MovieViewHolder> {

    private Context mContext;
    private ArrayList<News> mExampleList;
    private OnItemClickListener mListener;
    private OnItemLongClickListener mLongListener;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;

    public interface OnItemClickListener {
        void onItemClick(int position);
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

    public NewsAdapter(Context context, ArrayList<News> exampleList) {
        mContext = context;
        mExampleList = exampleList;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.news_item, parent, false);
        return new MovieViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {
        final News currentItem = mExampleList.get(position);

        holder.author.setText(currentItem.getSourceName());
        holder.title.setText(currentItem.getTitle());
        if(currentItem.getDescription()!=null && !currentItem.getDescription().equals("") && !currentItem.getDescription().equals("null")){
            holder.description.setText(currentItem.getDescription());
        }else{
            holder.description.setVisibility(View.GONE);
        }
        holder.publishedAt.setText(currentItem.getPublishedAt());
        if(currentItem.getImageUrl()!=null && !currentItem.getImageUrl().equals("") && !currentItem.getImageUrl().equals("null")){
            Picasso.get().load(currentItem.getImageUrl()).fit().into(holder.mImageView);
        }else{
            holder.mImageView.setVisibility(View.GONE);
        }

        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    holder.favorite.playAnimation();

                    News news = new News();

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Favorites").child(user.getUid()).child(currentItem.getTitle()).setValue(currentItem);

                }catch(Exception ex){
                    Toasty.error(mContext, ex.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    holder.bookmark.playAnimation();

                    News news = new News();

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Bookmark").child(user.getUid()).child(currentItem.getTitle()).setValue(currentItem);

                }catch(Exception ex){
                    Toasty.error(mContext, ex.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.share.playAnimation();

                Snackbar.make(v, "Preparing to share!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                if(currentItem.getImageUrl()!=null && !currentItem.getImageUrl().equals("") && !currentItem.getImageUrl().equals("null")){
                    shareItem(currentItem.imageUrl,currentItem.title,currentItem.contentUrl);
                }else{
                    shareTextItem(currentItem.title,currentItem.contentUrl);
                }
            }
        });

        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(mContext, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.report_option:
                                Toasty.error(mContext, "Reported successfully", Toast.LENGTH_SHORT, true).show();
                                break;
                        }
                        return true;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView author,title,description,publishedAt,buttonViewOption;
        LottieAnimationView favorite, bookmark, share;

        public MovieViewHolder(View itemView) {
            super(itemView);

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            //getting text views
            author = itemView.findViewById(R.id.author);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            mImageView = itemView.findViewById(R.id.urlToImage);
            publishedAt = itemView.findViewById(R.id.publishedAt);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);

            share = itemView.findViewById(R.id.share);
            bookmark = itemView.findViewById(R.id.bookmark);
            favorite = itemView.findViewById(R.id.favorite);

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

    public void shareTextItem(final String headline, final String url) {
        try {
            final String shareText = headline + "\n\nSource link:\n" + url + "\n\n Shared by " + user.getDisplayName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, shareText);
            mContext.startActivity(Intent.createChooser(i, "Share News"));
        }catch (Exception ex){
            Toasty.error(mContext,ex.getMessage(),Toast.LENGTH_SHORT, true).show();
        }
    }

    public void shareItem(String url, final String headline, final String description) {
        Picasso.get().load(url).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    final String shareText = headline + "\n\n" + description + "\n\n Shared by " + user.getDisplayName();
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.putExtra(Intent.EXTRA_TEXT, shareText);
                    i.setType("image/*");
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, mContext));
                    mContext.startActivity(Intent.createChooser(i, "Share News"));
                }catch (Exception ex){
                    Toasty.error(mContext,ex.getMessage(),Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

    public Uri getLocalBitmapUri(Bitmap bmp, final Context context) {
        Uri bmpUri = null;
        try {
            File file =  new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",file);;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


}
