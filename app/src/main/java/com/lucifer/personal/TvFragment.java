package com.lucifer.personal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TvFragment extends Fragment implements LiveTvAdapter.OnItemClickListener{
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ArrayList<LiveTv> tvList = new ArrayList<>();
    private LiveTvAdapter tvAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tv, container, false);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("LiveTv");

        RecyclerView recyclerView = rootView.findViewById(R.id.tv_channels);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvAdapter = new LiveTvAdapter(getContext(),tvList);
        recyclerView.setAdapter(tvAdapter);
        getData();
        tvAdapter.setOnItemClickListener(this);
        return rootView;
    }

    private void getData() {
        tvList.clear();

        final ProgressDialog pdLoading = new ProgressDialog(getContext());
        pdLoading.setMessage("\t Please wait loading...");
        pdLoading.setCancelable(false);
        pdLoading.show();
        try {

            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    tvList.clear();

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        LiveTv channel = postSnapshot.getValue(LiveTv.class);
                        tvList.add(channel);
                        tvAdapter.notifyDataSetChanged();
                    }

                    pdLoading.hide();
//                    if(swipeContainer.isRefreshing()){
//                        swipeContainer.setRefreshing(false);
//                    }

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

    @Override
    public void onItemClick(int position) {
        LiveTv channel = tvList.get(position);
        Intent detailIntent = new Intent(getContext(), VideoPlayActivity.class);
        detailIntent.putExtra("from", "tv");
        detailIntent.putExtra("country", channel.country);
        detailIntent.putExtra("description", channel.description);
        detailIntent.putExtra("language", channel.language);
        detailIntent.putExtra("imgUrl", channel.imgUrl);
        detailIntent.putExtra("launched", channel.launched);
        detailIntent.putExtra("name", channel.name);
        detailIntent.putExtra("network", channel.network);
        detailIntent.putExtra("owner", channel.owner);
        detailIntent.putExtra("videoId", channel.videoId);
        startActivity(detailIntent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}