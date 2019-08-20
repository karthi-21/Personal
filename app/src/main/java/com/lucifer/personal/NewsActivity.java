package com.lucifer.personal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.thefinestartist.finestwebview.FinestWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import am.appwise.components.ni.NoInternetDialog;
import es.dmoral.toasty.Toasty;

public class NewsActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener{

    private ShimmerRecyclerView shimmerRecycler;
    private RequestQueue mRequestQueue;
    private Drawer result = null;
    FirebaseAuth mAuth;
    private NewsAdapter newsAdapter;
    //the hero list where we will store all the hero objects after parsing json
    private ArrayList<News> newsArrayList;
    private SwipeRefreshLayout swipeContainer;
    private MaterialSearchView searchView;
    Toolbar toolbar;
    String lang;
    //for ads
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(this).build();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        try {
            toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Headlines");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            searchView = findViewById(R.id.search_view);
            searchView.setVoiceSearch(true);
            searchView.setCursorDrawable(R.drawable.custom_cursor);
            searchView.setEllipsize(true);
//            searchView.setSuggestions(sug);
            searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Intent intent = new Intent(NewsActivity.this,SearchResultsActivity.class);
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


        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        try {
            if (user == null) {
                Toast.makeText(this, "Please login again...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Please login again..." + e.getMessage(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
        }



        //initializing Recycler View and hero list
        shimmerRecycler = findViewById(R.id.shimmer_recycler_view);

        shimmerRecycler.setHasFixedSize(true);
        shimmerRecycler.setLayoutManager(new LinearLayoutManager(this));

        newsArrayList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);

        //this method will fetch and parse the data
        parseJSON(jsonUrl());

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                newsArrayList.clear();
                mRequestQueue = Volley.newRequestQueue(NewsActivity.this);
                parseJSON(jsonUrl());
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //fetchTimelineAsync(0);
            }
        });

        final FloatingActionMenu fabLang = findViewById(R.id.menu_lang);

        FloatingActionButton fabTamil = findViewById(R.id.fabTamil);
        fabTamil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lang != "ta") {
                    closed();
                    fabLang.close(true);
                    new PrefManager(NewsActivity.this).saveLangDetails("ta");
                    Toast.makeText(NewsActivity.this,"Changing to 'Tamil' Language",Toast.LENGTH_SHORT).show();
                    newsArrayList.clear();
                    mRequestQueue = Volley.newRequestQueue(NewsActivity.this);
                    parseJSON(jsonUrl());
                    showAds();

                }else{
                    Snackbar.make(v,"No changes made",3000).show();
                    fabLang.close(true);
                }
            }
        });


        FloatingActionButton fabEnglish = findViewById(R.id.fabEnglish);
        fabEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lang != "en") {
                    closed();
                    new PrefManager(NewsActivity.this).saveLangDetails("en");
                    fabLang.close(true);
                    Toast.makeText(NewsActivity.this, "Changing to 'English' Language", Toast.LENGTH_SHORT).show();
                    newsArrayList.clear();
                    mRequestQueue = Volley.newRequestQueue(NewsActivity.this);
                    parseJSON(jsonUrl());
                    showAds();
                }else{
                    Snackbar.make(v,"No changes made",3000).show();
                    fabLang.close(true);
                }
            }
        });

        FloatingActionButton fabTelugu = findViewById(R.id.fabTelugu);
        fabTelugu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lang != "te") {
                    closed();
                    new PrefManager(NewsActivity.this).saveLangDetails("te");
                    fabLang.close(true);
                    Toast.makeText(NewsActivity.this, "Changing to 'Telugu' Language", Toast.LENGTH_SHORT).show();
                    newsArrayList.clear();
                    mRequestQueue = Volley.newRequestQueue(NewsActivity.this);
                    parseJSON(jsonUrl());
                    showAds();
                }else{
                    Snackbar.make(v,"No changes made",3000).show();
                    fabLang.close(true);
                }
            }
        });

        FloatingActionButton fabMalayalam = findViewById(R.id.fabMalayalam);
        fabMalayalam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lang != "ml") {
                    closed();
                    new PrefManager(NewsActivity.this).saveLangDetails("ml");
                    fabLang.close(true);
                    Toast.makeText(NewsActivity.this, "Changing to 'Malayalam' Language", Toast.LENGTH_SHORT).show();
                    newsArrayList.clear();
                    mRequestQueue = Volley.newRequestQueue(NewsActivity.this);
                    parseJSON(jsonUrl());
                    showAds();
                }else{
                    Snackbar.make(v,"No changes made",3000).show();
                    fabLang.close(true);
                }
            }
        });

        FloatingActionButton fabKannada = findViewById(R.id.fabKannada);
        fabKannada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(lang != "kn") {
//                    new PrefManager(NewsActivity.this).saveLangDetails("kn");
//                    fabLang.close(true);
//                    Toast.makeText(NewsActivity.this, "Changing to 'Kannadam' Language", Toast.LENGTH_SHORT).show();
//                    newsArrayList.clear();
//                    mRequestQueue = Volley.newRequestQueue(NewsActivity.this);
//                    parseJSON(jsonUrl());
//                }else{
                Snackbar.make(v,"Working on it!",3000).show();
//                    fabLang.close(true);
//                }
            }
        });

        FloatingActionButton fabHindi = findViewById(R.id.fabHindi);
        fabHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lang != "hi") {
                    closed();
                    new PrefManager(NewsActivity.this).saveLangDetails("hi");
                    fabLang.close(true);
                    Toast.makeText(NewsActivity.this, "Changing to 'Hindi' Language", Toast.LENGTH_SHORT).show();
                    newsArrayList.clear();
                    mRequestQueue = Volley.newRequestQueue(NewsActivity.this);
                    parseJSON(jsonUrl());
                    showAds();
                }else{
                    Snackbar.make(v,"No changes made",3000).show();
                    fabLang.close(true);
                }
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    private void parseJSON(String url) {
        //getting the progressbar
        final ProgressDialog pdLoading = new ProgressDialog(NewsActivity.this);
        pdLoading.setMessage("\t Fetching latest NEWS updates...");
        pdLoading.setCancelable(false);
//        pdLoading.show();


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                        pdLoading.hide();
                        try {
                            //getting the whole json object from the response
                            JSONArray heroArray = response.getJSONArray("articles");

                            //now looping through all the elements of the json array
                            for (int i = 0; i < heroArray.length(); i++) {

                                //getting the json object of the particular index inside the array
                                JSONObject articles = heroArray.getJSONObject(i);

                                JSONObject source = articles.getJSONObject("source");

                                //declaring local variables
                                String title = articles.getString("title");
                                String description = articles.getString("description");
                                String contentUrl = articles.getString("url");
                                String imageUrl = articles.getString("image");
                                String publishedAt = articles.getString("publishedAt");
//                                try {
//                                    String dayOfTheWeek = (String) DateFormat.format("EEEE", Long.parseLong(publishedAt)); // Thursday
//                                }catch (Exception e){
//                                    Toast.makeText( )
//                                }

                                String name = source.getString("name");
                                String url = source.getString("url");
                                News hero = new News(title,description,contentUrl,imageUrl,publishedAt,name,url);
                                newsArrayList.add(hero);
                            }
                            //creating custom adapter object
                            newsAdapter = new NewsAdapter(NewsActivity.this,newsArrayList);
                            shimmerRecycler.setAdapter(newsAdapter);
                            if(swipeContainer.isRefreshing()) {
                                swipeContainer.setRefreshing(false);
                            }
                            newsAdapter.setOnItemClickListener(NewsActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(NewsActivity.this, error.getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                });

        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        News clickedItem = newsArrayList.get(position);
        new FinestWebView.Builder(NewsActivity.this).theme(R.style.FinestWebViewTheme)
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
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        }else if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String jsonUrl(){
        String url;

        if (!new PrefManager(this).isUserLogedOut()) {
            lang = new PrefManager(this).getLang();
            url = "https://gnews.io/api/v3/top-news?token=a12ce1f5d7001a7994bd0df87e501d7d&lang="+lang;
        }else{
            url = "https://gnews.io/api/v3/top-news?token=a12ce1f5d7001a7994bd0df87e501d7d&lang=ta";
        }

        return url;
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
}
