package com.lucifer.personal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, MoviesAdapter.OnItemClickListener {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    private long backPressedTime;
    private Toast backToast;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    FirebaseAuth mAuth;
    IProfile profile;
    private SwipeRefreshLayout swipeContainer;
    private MaterialSearchView searchView;
    Toolbar toolbar;
    private InterstitialAd mInterstitialAd;
    private SliderLayout mDemoSlider;
    private DatabaseReference mDatabase;
    ArrayList<Movie> listMovie = new ArrayList<>();
    ArrayList<Movie> listSliderMovies = new ArrayList<>();
    ArrayList<Movie> listActionMovie = new ArrayList<>();
    ArrayList<Movie> listDramaMovie = new ArrayList<>();
    ArrayList<Movie> listThrillerMovie = new ArrayList<>();
    ArrayList<Movie> listComedyMovie = new ArrayList<>();
    ArrayList<Movie> listHorrorMovie = new ArrayList<>();
    ArrayList<Movie> listLoveMovie = new ArrayList<>();
    ArrayList<Movie> listdubMovie = new ArrayList<>();
    RecyclerView actionRecyclerView, dramaRecyclerView, thrillerRecyclerView, comedyRecyclerView, horrorRecyclerView, loveRecyclerView, dubRecyclerView;
    MoviesAdapter actionMoviesAdapter, dramaMoviesAdapter, thrillerMoviesAdapter, comedyMoviesAdapter, horrorMoviesAdapter, loveMoviesAdapter, dubMoviesAdapter;
    LinearLayout moviesLayout, actionMoviesLayout, dramaMoviesLayout, thrillerMoviesLayout, comedyMoviesLayout, horrorMoviesLayout, loveMoviesLayout, dubMoviesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(this).build();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movie");

        try {
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            searchView = findViewById(R.id.search_view);
            searchView.setVoiceSearch(true);
            searchView.setCursorDrawable(R.drawable.custom_cursor);
            searchView.setEllipsize(true);
//            searchView.setSuggestions(sug);
            searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Intent intent = new Intent(MainActivity.this,SearchResultsActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //Do some magic
                    return true;
                }
            });

            searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
                @Override
                public void onSearchViewShown() {
                    //Do some magic
                }

                @Override
                public void onSearchViewClosed() {
                    //Do some magic
                }
            });
        }catch(Exception e){
            Log.e("SearchError",e.getMessage());
        }

        MobileAds.initialize(this,
                "ca-app-pub-4984184584221035~6823881381");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4984184584221035/5587246360");//example: ca-app-pub-3940256099942544/1033173712
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        try {
            if (user == null) {
                Toast.makeText(this, "Please login again...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                try {
                    profile = new ProfileDrawerItem().withName(user.getDisplayName()).withEmail(user.getEmail()).withIcon(user.getPhotoUrl());

                } catch (Exception e) {
                    Toast.makeText(this, "Please login again..." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                }
            }

            // Create the AccountHeader
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withTranslucentStatusBar(true)
                    .withHeaderBackground(R.drawable.header)
                    .addProfiles(
                            profile
                    )
                    .build();

            //Create the drawer
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withDrawerLayout(R.layout.crossfade_material_drawer)
                    .withHasStableIds(true)
                    .withDrawerWidthDp(72)
                    .withGenerateMiniDrawer(true)
                    .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName("Movies").withIcon(R.drawable.ic_movie).withIdentifier(0),
                            new PrimaryDrawerItem().withName("Headlines").withIcon(R.drawable.ic_news).withIdentifier(1),
                            new PrimaryDrawerItem().withName("My Favorites").withIcon(R.drawable.ic_star).withIdentifier(2),
                            new PrimaryDrawerItem().withName("My Bookmarks").withIcon(R.drawable.ic_bookmark).withIdentifier(3),
                            new PrimaryDrawerItem().withName("Live TV").withIcon(R.drawable.ic_live_tv).withIdentifier(4),
                            new PrimaryDrawerItem().withName("Setting").withIcon(R.drawable.ic_settings).withIdentifier(5),
//                        new PrimaryDrawerItem().withName("5th").withDescription("A more complex sample").withIcon(MaterialDesignIconic.Icon.gmi_adb).withIdentifier(5),
//                        new PrimaryDrawerItem().withName("6th").withIcon(MaterialDesignIconic.Icon.gmi_car).withIdentifier(6),
                            new DividerDrawerItem(),
                            new SecondaryDrawerItem().withName("Sign out").withIcon(R.drawable.ic_action_signing).withIdentifier(7).withSelectable(true)
                    ) // add the items we want to use with our Drawer
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (drawerItem.getIdentifier() == 7) {
                                if (user != null) {
                                    Toasty.warning(MainActivity.this, "See you soon..." + user.getDisplayName(), Toast.LENGTH_SHORT, true).show();
//                                Toast.makeText(MainActivity.this,"See you soon..."+user.getDisplayName(),Toast.LENGTH_SHORT).show();
                                }
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                            } else if (drawerItem.getIdentifier() == 1) {
                                result.closeDrawer();
                                startActivity(new Intent(MainActivity.this, NewsActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } else if (drawerItem.getIdentifier() == 2) {
                                result.closeDrawer();
                                startActivity(new Intent(MainActivity.this, FavroitesActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } else if (drawerItem.getIdentifier() == 3) {
                                result.closeDrawer();
                                startActivity(new Intent(MainActivity.this, BookmarkActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } else if (drawerItem.getIdentifier() == 4) {
                                result.closeDrawer();
                                startActivity(new Intent(MainActivity.this, LiveTVActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }else if (drawerItem.getIdentifier() == 5) {
                                result.closeDrawer();
                                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            } else {
                                result.closeDrawer();
                            }

                            return false;
                        }
                    })
                    .withSavedInstance(savedInstanceState)
                    .withShowDrawerOnFirstLaunch(true)
                    .build();

            //get out our drawerLayout
            crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

            //define maxDrawerWidth
            crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));
            //add second view (which is the miniDrawer)
            MiniDrawer miniResult = result.getMiniDrawer();
            //build the view for the MiniDrawer
            View view = miniResult.build(this);
            //set the background of the MiniDrawer as this would be transparent
            view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
            //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
            crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
            miniResult.withCrossFader(new ICrossfader() {
                @Override
                public void crossfade() {
                    boolean isFaded = isCrossfaded();
                    crossfadeDrawerLayout.crossfade(400);

                    //only close the drawer if we were already faded and want to close it now
                    if (isFaded) {
                        result.getDrawerLayout().closeDrawer(GravityCompat.START);
                    }
                }

                @Override
                public boolean isCrossfaded() {
                    return crossfadeDrawerLayout.isCrossfaded();
                }
            });

            //hook to the crossfade event
            crossfadeDrawerLayout.withCrossfadeListener(new CrossfadeDrawerLayout.CrossfadeListener() {
                @Override
                public void onCrossfade(View containerView, float currentSlidePercentage, int slideOffset) {
                    //Log.e("CrossfadeDrawerLayout", "crossfade: " + currentSlidePercentage + " - " + slideOffset);
                }
            });
        }catch(Exception e){
            Log.e("DrawerError",e.getMessage());
        }


        //slider initialization
        mDemoSlider = findViewById(R.id.slider);

        moviesLayout = findViewById(R.id.moviesLayout);
        actionMoviesLayout = findViewById(R.id.actionMoviesLayout);
        dramaMoviesLayout = findViewById(R.id.dramaMoviesLayout);
        thrillerMoviesLayout = findViewById(R.id.thrillerMoviesLayout);
        comedyMoviesLayout = findViewById(R.id.comedyMoviesLayout);
        horrorMoviesLayout = findViewById(R.id.horrorMoviesLayout);
        loveMoviesLayout = findViewById(R.id.loveMoviesLayout);
        dubMoviesLayout = findViewById(R.id.dubMoviesLayout);

        actionRecyclerView = findViewById(R.id.action_recycler_view);
        dramaRecyclerView = findViewById(R.id.drama_recycler_view);
        thrillerRecyclerView = findViewById(R.id.thriller_recycler_view);
        comedyRecyclerView = findViewById(R.id.comedy_recycler_view);
        horrorRecyclerView = findViewById(R.id.horror_recycler_view);
        loveRecyclerView = findViewById(R.id.love_recycler_view);
        dubRecyclerView = findViewById(R.id.dub_recycler_view);

        actionRecyclerView.setHasFixedSize(true);
        dramaRecyclerView.setHasFixedSize(true);
        thrillerRecyclerView.setHasFixedSize(true);
        comedyRecyclerView.setHasFixedSize(true);
        horrorRecyclerView.setHasFixedSize(true);
        loveRecyclerView.setHasFixedSize(true);
        dubRecyclerView.setHasFixedSize(true);

        actionRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        dramaRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        thrillerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        comedyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        horrorRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        loveRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        dubRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        actionMoviesAdapter = new MoviesAdapter(MainActivity.this,listActionMovie);
        dramaMoviesAdapter = new MoviesAdapter(MainActivity.this,listDramaMovie);
        thrillerMoviesAdapter = new MoviesAdapter(MainActivity.this,listThrillerMovie);
        comedyMoviesAdapter = new MoviesAdapter(MainActivity.this,listComedyMovie);
        horrorMoviesAdapter = new MoviesAdapter(MainActivity.this,listHorrorMovie);
        loveMoviesAdapter = new MoviesAdapter(MainActivity.this,listLoveMovie);
        dubMoviesAdapter = new MoviesAdapter(MainActivity.this,listdubMovie);

        actionRecyclerView.setAdapter(actionMoviesAdapter);
        dramaRecyclerView.setAdapter(dramaMoviesAdapter);
        thrillerRecyclerView.setAdapter(thrillerMoviesAdapter);
        comedyRecyclerView.setAdapter(comedyMoviesAdapter);
        horrorRecyclerView.setAdapter(horrorMoviesAdapter);
        loveRecyclerView.setAdapter(loveMoviesAdapter);
        dubRecyclerView.setAdapter(dubMoviesAdapter);

        getSlider();
        getData();

        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDemoSlider.removeAllSliders();
                getData();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        actionMoviesAdapter.setOnItemClickListener(MainActivity.this);
        dramaMoviesAdapter.setOnItemClickListener(MainActivity.this);
        thrillerMoviesAdapter.setOnItemClickListener(MainActivity.this);
        comedyMoviesAdapter.setOnItemClickListener(MainActivity.this);
        horrorMoviesAdapter.setOnItemClickListener(MainActivity.this);
        loveMoviesAdapter.setOnItemClickListener(MainActivity.this);
        dubMoviesAdapter.setOnItemClickListener(MainActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
//        outState.putString(TEXT_KEY, editText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        }else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
//            super.onBackPressed();
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                backToast.cancel();
                super.onBackPressed();
                return;
            } else {
                backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
                backToast.show();
            }

            backPressedTime = System.currentTimeMillis();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        editText.setText(savedInstanceState.getString(TEXT_KEY));
//        editText.setSelection(editText.getText().length());
    }

    public void showAds(){

        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }

    }

    public void closed(){
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    public void getSlider() {
        listSliderMovies.clear();

        try {

            FirebaseDatabase.getInstance().getReference().child("Slider").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    listSliderMovies.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        Movie movie = postSnapshot.getValue(Movie.class);
                        listSliderMovies.add(movie);
                    }

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.centerCrop();

                    for (int i = 0; i < listSliderMovies.size(); i++) {
                        TextSliderView sliderView = new TextSliderView(MainActivity.this);

                        sliderView
                                .description(listSliderMovies.get(i).movieName)
                                .image(listSliderMovies.get(i).imgUrl)
                                .setOnSliderClickListener(MainActivity.this);

                        sliderView.bundle(new Bundle());
                        sliderView.getBundle().putString("movie", listSliderMovies.get(i).movieName);
                        mDemoSlider.addSlider(sliderView);
                    }

                    mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                    mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                    mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                    mDemoSlider.setDuration(5000);
                    mDemoSlider.addOnPageChangeListener(MainActivity.this);

                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("FireBaseError", "loadSlider:onCancelled", databaseError.toException());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getData() {
        listMovie.clear();
        listActionMovie.clear();
        listDramaMovie.clear();
        listThrillerMovie.clear();
        listHorrorMovie.clear();
        listLoveMovie.clear();
        listComedyMovie.clear();
        listdubMovie.clear();

        final ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        pdLoading.setMessage("\t Please wait loading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
        try {

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    listMovie.clear();
                    listActionMovie.clear();
                    listDramaMovie.clear();
                    listThrillerMovie.clear();
                    listHorrorMovie.clear();
                    listLoveMovie.clear();
                    listComedyMovie.clear();
                    listdubMovie.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        Movie movie = postSnapshot.getValue(Movie.class);
                        listMovie.add(movie);
                        if(movie != null){
                            if(movie.type.equals("Action")){
                                listActionMovie.add(movie);
                            }
                            if(movie.type.equals("Thriller")){
                                listThrillerMovie.add(movie);
                            }
                            if(movie.type.equals("Drama")){
                                listDramaMovie.add(movie);
                            }
                            if(movie.type.equals("Comedy")){
                                listComedyMovie.add(movie);
                            }
                            if(movie.type.equals("Horror")){
                                listHorrorMovie.add(movie);
                            }
                            if(movie.type.equals("Love")){
                                listLoveMovie.add(movie);
                            }
                            if(movie.dubbed.equals("Yes")){
                                listdubMovie.add(movie);
                            }
                        }

                        actionMoviesAdapter.notifyDataSetChanged();
                        dramaMoviesAdapter.notifyDataSetChanged();
                        thrillerMoviesAdapter.notifyDataSetChanged();
                        comedyMoviesAdapter.notifyDataSetChanged();
                        horrorMoviesAdapter.notifyDataSetChanged();
                        loveMoviesAdapter.notifyDataSetChanged();
                        dubMoviesAdapter.notifyDataSetChanged();

                        setVisibility();
                    }

                    pdLoading.hide();
                    if(swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("FireBaseError", "loadSlider:onCancelled", databaseError.toException());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVisibility() {
        if(listMovie.size()>0){

            moviesLayout.setVisibility(View.VISIBLE);

            if(listActionMovie.size() <= 0){
                actionMoviesLayout.setVisibility(View.GONE);
            }else{
                actionMoviesLayout.setVisibility(View.VISIBLE);
            }
            if(listComedyMovie.size() <= 0){
                comedyMoviesLayout.setVisibility(View.GONE);
            }else{
                comedyMoviesLayout.setVisibility(View.VISIBLE);
            }
            if(listDramaMovie.size() <= 0){
                dramaMoviesLayout.setVisibility(View.GONE);
            }else{
                dramaMoviesLayout.setVisibility(View.VISIBLE);
            }
            if(listHorrorMovie.size() <= 0){
                horrorMoviesLayout.setVisibility(View.GONE);
            }else{
                horrorMoviesLayout.setVisibility(View.VISIBLE);
            }
            if(listLoveMovie.size() <= 0){
                loveMoviesLayout.setVisibility(View.GONE);
            }else{
                loveMoviesLayout.setVisibility(View.VISIBLE);
            }
            if(listThrillerMovie.size() <= 0){
                thrillerMoviesLayout.setVisibility(View.GONE);
            }else{
                thrillerMoviesLayout.setVisibility(View.VISIBLE);
            }
        } else {
            moviesLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
//
//        Toast.makeText(this,slider.getBundle().get("movie") + "",Toast.LENGTH_SHORT).show();

        for(int i=0; i < listSliderMovies.size(); i++){
            if( listSliderMovies.get(i).movieName == slider.getBundle().get("movie")) {
                Intent detailIntent = new Intent(MainActivity.this, VideoPlayActivity.class);
                detailIntent.putExtra("movieName", listSliderMovies.get(i).movieName);
                detailIntent.putExtra("director", listSliderMovies.get(i).director);
                detailIntent.putExtra("duration", listSliderMovies.get(i).duration);
                detailIntent.putExtra("imgUrl", listSliderMovies.get(i).imgUrl);
                detailIntent.putExtra("lang", listSliderMovies.get(i).lang);
                detailIntent.putExtra("star", listSliderMovies.get(i).star);
                detailIntent.putExtra("type", listSliderMovies.get(i).type);
                detailIntent.putExtra("utubeId", listSliderMovies.get(i).utubeId);
                detailIntent.putExtra("videoUrl", listSliderMovies.get(i).videoUrl);
                startActivity(detailIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onItemClick(int position, String type, String dubbed) {
        Movie item = new Movie();
        if(!type.equals("") && !dubbed.equals("Yes")){
            if(type.equals("Action")){
                item = listActionMovie.get(position);
            }
            if(type.equals("Thriller")){
                item = listThrillerMovie.get(position);
            }
            if(type.equals("Drama")){
                item = listDramaMovie.get(position);
            }
            if(type.equals("Comedy")){
                item = listComedyMovie.get(position);
            }
            if(type.equals("Horror")){
                item = listHorrorMovie.get(position);
            }
            if(type.equals("Love")){
                item = listLoveMovie.get(position);
            }
        }else if(dubbed.equals("Yes")){
            item = listdubMovie.get(position);
        }

        Intent detailIntent = new Intent(MainActivity.this, VideoPlayActivity.class);
        detailIntent.putExtra("movieName", item.movieName);
        detailIntent.putExtra("director", item.director);
        detailIntent.putExtra("duration", item.duration);
        detailIntent.putExtra("imgUrl", item.imgUrl);
        detailIntent.putExtra("lang", item.lang);
        detailIntent.putExtra("star", item.star);
        detailIntent.putExtra("type", item.type);
        detailIntent.putExtra("utubeId", item.utubeId);
        detailIntent.putExtra("description", item.description);
        detailIntent.putExtra("videoUrl", item.videoUrl);
        startActivity(detailIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}
