package com.lucifer.personal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.thefinestartist.finestwebview.FinestWebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import es.dmoral.toasty.Toasty;

public class BookmarkActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener, NewsAdapter.OnItemLongClickListener  {

    private RecyclerView shimmerRecycler;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    FirebaseAuth mAuth;
    ImageView noData;
    private NewsAdapter newsAdapter;
    //the hero list where we will store all the hero objects after parsing json
    private ArrayList<News> newsArrayList;
    private SwipeRefreshLayout swipeContainer;

    private DatabaseReference mDatabase;
    FirebaseUser user;

    AlertDialog.Builder alertBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(this).build();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Bookmark").child(user.getUid());

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Bookmarks");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initializing Recycler View and hero list
        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);
        noData = findViewById(R.id.no_data);
        swipeContainer = findViewById(R.id.swipeContainer);
        alertBuilder = new AlertDialog.Builder(this,R.style.ThemeOverlay_MaterialComponents_Dialog_Alert);
        shimmerRecycler.setHasFixedSize(true);
        shimmerRecycler.setLayoutManager(new LinearLayoutManager(this));

        newsArrayList = new ArrayList<>();
        newsAdapter = new NewsAdapter(BookmarkActivity.this, newsArrayList);


        shimmerRecycler.setAdapter(newsAdapter);
        getData();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsArrayList.clear();
                getData();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        newsAdapter.setOnItemClickListener(BookmarkActivity.this);
        newsAdapter.setOnItemLongClickListener(BookmarkActivity.this);
    }


    private void getData() {
        final ProgressDialog pdLoading = new ProgressDialog(BookmarkActivity.this);
        pdLoading.setMessage("\t Gathering our Bookmarks...");
        pdLoading.setCancelable(false);
        pdLoading.show();

        newsArrayList.clear();

        try {

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    pdLoading.hide();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        News hero = postSnapshot.getValue(News.class);
                        newsArrayList.add(hero);
                        newsAdapter.notifyDataSetChanged();
                    }
                    if(!dataSnapshot.exists()){
                        shimmerRecycler.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                    Log.w("FireBaseError", "loadPost:onCancelled", databaseError.toException());

                }
            });

            if (swipeContainer.isRefreshing()) {
                swipeContainer.setRefreshing(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onItemClick(int position) {
        News clickedItem = newsArrayList.get(position);
        new FinestWebView.Builder(BookmarkActivity.this).theme(R.style.FinestWebViewTheme)
                .titleDefault("News-X")
                .toolbarScrollFlags(0)
                .statusBarColorRes(R.color.primary_dark)
                .toolbarColorRes(R.color.primary_dark)
                .titleColorRes(R.color.finestWhite)
                .urlColorRes(R.color.primary_light)
                .iconDefaultColorRes(R.color.finestWhite)
                .progressBarColorRes(R.color.finestWhite)
                .swipeRefreshColorRes(R.color.primary_dark)
                .menuSelector(R.drawable.selector_dark_theme)
                .menuTextGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT)
                .menuTextPaddingRightRes(R.dimen.defaultMenuTextPaddingLeft)
                .dividerHeight(0)
                .gradientDivider(false)
                .setCustomAnimations(R.anim.slide_up, R.anim.hold, R.anim.hold, R.anim.slide_down)
//                .setCustomAnimations(R.anim.slide_left_in, R.anim.hold, R.anim.hold,
//                        R.anim.slide_right_out)
//                .setCustomAnimations(R.anim.fade_in_fast, R.anim.fade_out_medium, R.anim.fade_in_medium, R.anim.fade_out_fast)
                .show(clickedItem.getContentUrl());
    }

    @Override
    public void onItemLongClick(final int position) {

        try {
            new FancyAlertDialog.Builder(this)
                    .setTitle("Confirm Remove")
                    .setBackgroundColor(Color.parseColor("#392A53"))  //Don't pass R.color.colorvalue
                    .setMessage("Do you really want to remove the bookmark?")
                    .setPositiveBtnBackground(Color.parseColor("#C50B0B"))  //Don't pass R.color.colorvalue
                    .setPositiveBtnText("Yes,Remove")
                    .setNegativeBtnText("No, don't")
                    .setAnimation(Animation.POP)
                    .isCancellable(false)
                    .setIcon(R.drawable.ic_warning, Icon.Visible)
                    .OnPositiveClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            final News clickedItem = newsArrayList.get(position);
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        // TODO: handle the post
                                        News hero = postSnapshot.getValue(News.class);
                                        if (hero.getTitle() == clickedItem.getTitle()) {

                                            postSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    Toast.makeText(BookmarkActivity.this, "Your Bookmark removed", Toast.LENGTH_LONG).show();
                                                    getData();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(BookmarkActivity.this, "Some error occured while removing bookmark", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            Toast.makeText(getApplicationContext(),"You saved your Bookmark :)",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build();
//            alertBuilder.setMessage("Do you really want to remove the bookmark?")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            final News clickedItem = newsArrayList.get(position);
//                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//                                        // TODO: handle the post
//                                        News hero = postSnapshot.getValue(News.class);
//                                        if (hero.getTitle() == clickedItem.getTitle()) {
//
//                                            postSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
//                                                @Override
//                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                                                    Toast.makeText(BookmarkActivity.this, "Bookmark removed", Toast.LENGTH_LONG).show();
//                                                    getData();
//                                                }
//                                            });
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//                                    Toast.makeText(BookmarkActivity.this, "Some error occured while removing bookmark", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                        }
//                    });
//            //Creating dialog box
//            AlertDialog alert = alertBuilder.create();
//            //Setting the title manually
//            alert.setTitle("Remove Bookmark");
//            alert.show();
        }catch (Exception e){
            Log.e("alert",e.getMessage());
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
                    i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, BookmarkActivity.this));
                    startActivity(Intent.createChooser(i, "Share News"));
                }catch (Exception ex){
                    Toasty.error(BookmarkActivity.this,ex.getMessage(), Toast.LENGTH_SHORT, true).show();
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
            File file =  new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(BookmarkActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

