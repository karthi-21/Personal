package com.lucifer.personal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    private long backPressedTime;
    private Toast backToast;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    FirebaseAuth mAuth;
    IProfile profile;
    private MaterialSearchView searchView;
    Toolbar toolbar;
    private InterstitialAd mInterstitialAd;
    DatabaseReference mDatabase;

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

        //loading the default fragment
        loadFragment(new MoviesFragment());

        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(this);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_movies:
                fragment = new MoviesFragment();
                break;

            case R.id.navigation_videos:
                fragment = new VideosFragment();
                break;

            case R.id.navigation_songs:
                fragment = new SongsFragment();
                break;

            case R.id.navigation_tv:
                fragment = new TvFragment();
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
