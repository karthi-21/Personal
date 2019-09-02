package com.lucifer.personal;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class SongsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);


//        try {
//            OkHttpClient client = new OkHttpClient();
//
//            Request request = new Request.Builder()
//                    .url("https://devru-gaana-v1.p.rapidapi.com/newReleases.php")
//                    .get()
//                    .addHeader("x-rapidapi-host", "devru-gaana-v1.p.rapidapi.com")
//                    .addHeader("x-rapidapi-key", "")
//                    .build();
//
//            Call callToUrl = client.newCall(request);
//
//            callToUrl.enqueue(new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    Log.e("Error", "onFailure: " + e.getMessage() );
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//
//                    try {
//                        String jsonData = response.body().string();
//                        JSONObject Jobject = new JSONObject(jsonData);
//                        JSONArray Jarray = Jobject.getJSONArray("album");
//
//                        for (int i = 0; i < Jarray.length(); i++) {
//                            JSONObject object = Jarray.getJSONObject(i);
//                        }
//                    }catch (JSONException e){
//                        Log.e("Exception", "jsonCatch: " + e.getMessage() );
//                    }
//                }
//            });
//
//        }catch (Exception e){
//            Log.e("Error", "outterCatch: " + e.getMessage() );
//        }

        return rootView;
    }
}