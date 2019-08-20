package com.lucifer.personal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.thefinestartist.finestwebview.FinestWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import am.appwise.components.ni.NoInternetDialog;
import es.dmoral.toasty.Toasty;

public class SearchResultsActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private RequestQueue mRequestQueue;
    private NewsAdapter newsAdapter;
    private ArrayList<News> newsArrayList;
    private SwipeRefreshLayout swipeContainer;
    Toolbar toolbar;
    String query = "";
    String lang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent intent = getIntent();
        query = intent.getStringExtra("query");

//        String JSON_URL = "https://newsapi.org/v2/everything?q=" + query + "&sortBy=popularity&apiKey=33f3a0195d484ea0928cfad58fca34d7";

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Search results...");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(this).build();

        //initializing Recycler View and hero list
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsArrayList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(this);

        //this method will fetch and parse the data
        parseJSON(jsonUrl(query));

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                newsArrayList.clear();
                mRequestQueue = Volley.newRequestQueue(SearchResultsActivity.this);
                parseJSON(jsonUrl(query));
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void parseJSON(String url) {
        //getting the progressbar
        final ProgressDialog pdLoading = new ProgressDialog(SearchResultsActivity.this);
        pdLoading.setMessage("\t Searching about"+query+"...");
        pdLoading.setCancelable(false);
        pdLoading.show();



        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pdLoading.hide();
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
                                String name = source.getString("name");
                                String url = source.getString("url");
                                News hero = new News(title,description,contentUrl,imageUrl,publishedAt,name,url);
                                newsArrayList.add(hero);
                            }
                            //creating custom adapter object
                            newsAdapter = new NewsAdapter(SearchResultsActivity.this,newsArrayList);
                            recyclerView.setAdapter(newsAdapter);
                            if(swipeContainer.isRefreshing()) {
                                swipeContainer.setRefreshing(false);
                            }
                            newsAdapter.setOnItemClickListener(SearchResultsActivity.this);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(SearchResultsActivity.this, error.getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                });

        mRequestQueue.add(request);
    }

    @Override
    public void onItemClick(int position) {
        News clickedItem = newsArrayList.get(position);
        new FinestWebView.Builder(SearchResultsActivity.this).theme(R.style.FinestWebViewTheme)
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
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public String jsonUrl(String query){
        String url;

        if (!new PrefManager(this).isUserLogedOut()) {
            lang = new PrefManager(this).getLang();
            url = "https://gnews.io/api/v3/search?q="+query+"&token=a12ce1f5d7001a7994bd0df87e501d7d&lang="+lang;

        }else{
            url = "https://gnews.io/api/v3/search?q="+query+"&token=a12ce1f5d7001a7994bd0df87e501d7d&lang=en";
        }

        return url;
    }
}
