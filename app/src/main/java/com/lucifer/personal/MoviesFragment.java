package com.lucifer.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lucifer.personal.R;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

public class MoviesFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, MoviesAdapter.OnItemClickListener {
    FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeContainer;
    private InterstitialAd mInterstitialAd;
    private SliderLayout mDemoSlider;
    private DatabaseReference mDatabase;
    private ArrayList<Movie> listMovie = new ArrayList<>();
    private ArrayList<Movie> listSliderMovies = new ArrayList<>();
    private ArrayList<Movie> listActionMovie = new ArrayList<>();
    private ArrayList<Movie> listDramaMovie = new ArrayList<>();
    private ArrayList<Movie> listThrillerMovie = new ArrayList<>();
    private ArrayList<Movie> listComedyMovie = new ArrayList<>();
    private ArrayList<Movie> listHorrorMovie = new ArrayList<>();
    private ArrayList<Movie> listLoveMovie = new ArrayList<>();
    private ArrayList<Movie> listdubMovie = new ArrayList<>();
    private MoviesAdapter actionMoviesAdapter, dramaMoviesAdapter, thrillerMoviesAdapter, comedyMoviesAdapter, horrorMoviesAdapter, loveMoviesAdapter, dubMoviesAdapter;
    private LinearLayout moviesLayout, actionMoviesLayout, dramaMoviesLayout, thrillerMoviesLayout, comedyMoviesLayout, horrorMoviesLayout, loveMoviesLayout, dubMoviesLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies, container, false);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Movie");

        //slider initialization
        mDemoSlider = rootView.findViewById(R.id.slider);

        moviesLayout = rootView.findViewById(R.id.moviesLayout);
        actionMoviesLayout = rootView.findViewById(R.id.actionMoviesLayout);
        dramaMoviesLayout = rootView.findViewById(R.id.dramaMoviesLayout);
        thrillerMoviesLayout = rootView.findViewById(R.id.thrillerMoviesLayout);
        comedyMoviesLayout = rootView.findViewById(R.id.comedyMoviesLayout);
        horrorMoviesLayout = rootView.findViewById(R.id.horrorMoviesLayout);
        loveMoviesLayout = rootView.findViewById(R.id.loveMoviesLayout);
        dubMoviesLayout = rootView.findViewById(R.id.dubMoviesLayout);

        RecyclerView actionRecyclerView = rootView.findViewById(R.id.action_recycler_view);
        RecyclerView dramaRecyclerView = rootView.findViewById(R.id.drama_recycler_view);
        RecyclerView thrillerRecyclerView = rootView.findViewById(R.id.thriller_recycler_view);
        RecyclerView comedyRecyclerView = rootView.findViewById(R.id.comedy_recycler_view);
        RecyclerView horrorRecyclerView = rootView.findViewById(R.id.horror_recycler_view);
        RecyclerView loveRecyclerView = rootView.findViewById(R.id.love_recycler_view);
        RecyclerView dubRecyclerView = rootView.findViewById(R.id.dub_recycler_view);

        actionRecyclerView.setHasFixedSize(true);
        dramaRecyclerView.setHasFixedSize(true);
        thrillerRecyclerView.setHasFixedSize(true);
        comedyRecyclerView.setHasFixedSize(true);
        horrorRecyclerView.setHasFixedSize(true);
        loveRecyclerView.setHasFixedSize(true);
        dubRecyclerView.setHasFixedSize(true);

        actionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        dramaRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        thrillerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        comedyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        horrorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        loveRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        dubRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));

        actionMoviesAdapter = new MoviesAdapter(getContext(),listActionMovie);
        dramaMoviesAdapter = new MoviesAdapter(getContext(),listDramaMovie);
        thrillerMoviesAdapter = new MoviesAdapter(getContext(),listThrillerMovie);
        comedyMoviesAdapter = new MoviesAdapter(getContext(),listComedyMovie);
        horrorMoviesAdapter = new MoviesAdapter(getContext(),listHorrorMovie);
        loveMoviesAdapter = new MoviesAdapter(getContext(),listLoveMovie);
        dubMoviesAdapter = new MoviesAdapter(getContext(),listdubMovie);

        actionRecyclerView.setAdapter(actionMoviesAdapter);
        dramaRecyclerView.setAdapter(dramaMoviesAdapter);
        thrillerRecyclerView.setAdapter(thrillerMoviesAdapter);
        comedyRecyclerView.setAdapter(comedyMoviesAdapter);
        horrorRecyclerView.setAdapter(horrorMoviesAdapter);
        loveRecyclerView.setAdapter(loveMoviesAdapter);
        dubRecyclerView.setAdapter(dubMoviesAdapter);

        getSlider();
        getData();


        swipeContainer = rootView.findViewById(R.id.swipeContainer);
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

        actionMoviesAdapter.setOnItemClickListener(this);
        dramaMoviesAdapter.setOnItemClickListener(this);
        thrillerMoviesAdapter.setOnItemClickListener(this);
        comedyMoviesAdapter.setOnItemClickListener(this);
        horrorMoviesAdapter.setOnItemClickListener(this);
        loveMoviesAdapter.setOnItemClickListener(this);
        dubMoviesAdapter.setOnItemClickListener(this);

        //just change the fragment_dashboard
        //with the fragment you want to inflate
        //like if the class is com.lucifer.personal.MoviesFragment it should have R.layout.home_fragment
        //if it is DashboardFragment it should have R.layout.fragment_dashboard
//        return inflater.inflate(R.layout.fragment_movies, null);
        return rootView;
    }

    private void getSlider() {
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
                    try{
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.centerCrop();

                        for (int i = 0; i < listSliderMovies.size(); i++) {
                            TextSliderView sliderView = new TextSliderView(getContext());

                            sliderView
                                    .description(listSliderMovies.get(i).movieName)
                                    .image(listSliderMovies.get(i).imgUrl)
                                    .setOnSliderClickListener(MoviesFragment.this);

                            sliderView.bundle(new Bundle());
                            sliderView.getBundle().putString("movie", listSliderMovies.get(i).movieName);
                            mDemoSlider.addSlider(sliderView);
                        }

                        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                        mDemoSlider.setDuration(8000);
                        mDemoSlider.addOnPageChangeListener(MoviesFragment.this);
                    }catch(Exception e){
                        Log.e("slider", "onCreateView: " + e.getMessage() );
                    }

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

    private void getData() {
        listMovie.clear();
        listActionMovie.clear();
        listDramaMovie.clear();
        listThrillerMovie.clear();
        listHorrorMovie.clear();
        listLoveMovie.clear();
        listComedyMovie.clear();
        listdubMovie.clear();

        final ProgressDialog pdLoading = new ProgressDialog(getContext());
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

    private void setVisibility() {
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
            if(listdubMovie.size() <= 0){
                dubMoviesLayout.setVisibility(View.GONE);
            }else{
                dubMoviesLayout.setVisibility(View.VISIBLE);
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
                Intent detailIntent = new Intent(getContext(), VideoPlayActivity.class);
                detailIntent.putExtra("from", "movie");
                detailIntent.putExtra("movieName", listSliderMovies.get(i).movieName);
                detailIntent.putExtra("director", listSliderMovies.get(i).director);
                detailIntent.putExtra("duration", listSliderMovies.get(i).duration);
                detailIntent.putExtra("imgUrl", listSliderMovies.get(i).imgUrl);
                detailIntent.putExtra("lang", listSliderMovies.get(i).lang);
                detailIntent.putExtra("star", listSliderMovies.get(i).star);
                detailIntent.putExtra("type", listSliderMovies.get(i).type);
                detailIntent.putExtra("uTubeId", listSliderMovies.get(i).utubeId);
                detailIntent.putExtra("videoUrl", listSliderMovies.get(i).videoUrl);
                detailIntent.putExtra("description", listSliderMovies.get(i).description);
                startActivity(detailIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

        Intent detailIntent = new Intent(getContext(), VideoPlayActivity.class);
        detailIntent.putExtra("from", "movie");
        detailIntent.putExtra("movieName", item.movieName);
        detailIntent.putExtra("director", item.director);
        detailIntent.putExtra("duration", item.duration);
        detailIntent.putExtra("imgUrl", item.imgUrl);
        detailIntent.putExtra("lang", item.lang);
        detailIntent.putExtra("star", item.star);
        detailIntent.putExtra("type", item.type);
        detailIntent.putExtra("uTubeId", item.utubeId);
        detailIntent.putExtra("description", item.description);
        detailIntent.putExtra("videoUrl", item.videoUrl);
        startActivity(detailIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}