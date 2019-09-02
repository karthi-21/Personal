package com.lucifer.personal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import am.appwise.components.ni.NoInternetDialog;

public class VideoPlayActivity extends AppCompatActivity {

    YouTubePlayerView youTubePlayerView;
    private RelativeLayout layout;
    ImageButton pip, back;
    String imgUrl, uTubeId, videoUrl, description;
    TextView mActor, mName, mDirector, mDuration, mLanguage, mType, mDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        layout = findViewById(R.id.parentlayout);
        pip = findViewById(R.id.pip);
        back = findViewById(R.id.back);

        mActor = findViewById(R.id.actor);
        mName = findViewById(R.id.movieName);
        mDirector = findViewById(R.id.director);
        mDuration = findViewById(R.id.duration);
        mLanguage = findViewById(R.id.lang);
        mType = findViewById(R.id.type);
        mDesc = findViewById(R.id.description);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(this).build();

        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if(from.equals("movie")){
            imgUrl = intent.getStringExtra("imgUrl");
            uTubeId = intent.getStringExtra("uTubeId");
            videoUrl = intent.getStringExtra("videoUrl");

            mName.setText(intent.getStringExtra("movieName"));
            String act = "<b>Starer</b> :" + intent.getStringExtra("star");
            mActor.setText(Html.fromHtml(act));
            String direct = "<b>Directer</b> :" + intent.getStringExtra("director");
            mDirector.setText(Html.fromHtml(direct));
            String dur = "<b>Duration</b> :" + intent.getStringExtra("duration");
            mDuration.setText(Html.fromHtml(dur));
            String typ = "<b>Type</b> :" + intent.getStringExtra("type");
            mType.setText(Html.fromHtml(typ));
            String lng = "<b>Language</b> :" + intent.getStringExtra("lang");
            mLanguage.setText(Html.fromHtml(lng));
            String des = "<b>Description</b> :\t\t" + intent.getStringExtra("description");
            mDesc.setText(Html.fromHtml(des));
        }
        if(from.equals("tv")) {
            uTubeId = intent.getStringExtra("videoId");
            mName.setText(intent.getStringExtra("name"));
            String act = "<b>Language</b> :" + intent.getStringExtra("language");
            mActor.setText(Html.fromHtml(act));
            String direct = "<b>Country</b> :" + intent.getStringExtra("country");
            mDirector.setText(Html.fromHtml(direct));
            String dur = "<b>Launched</b> :" + intent.getStringExtra("launched");
            mDuration.setText(Html.fromHtml(dur));
            String typ = "<b>Network</b> :" + intent.getStringExtra("network");
            mType.setText(Html.fromHtml(typ));
            String lng = "<b>Owned By</b> :" + intent.getStringExtra("owner");
            mLanguage.setText(Html.fromHtml(lng));
            String des = "<b>Description</b> :\t\t" + intent.getStringExtra("description");
            mDesc.setText(Html.fromHtml(des));
        }


        youTubePlayerView = findViewById(R.id.youtube_player_view);
        getLifecycle().addObserver(youTubePlayerView);

        if(from.equals("tv")){
           youTubePlayerView.getPlayerUiController().enableLiveVideoUi(true);
        }
        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(uTubeId, 0);

                YouTubePlayerUtils.loadOrCueVideo(youTubePlayer,getLifecycle(), uTubeId,0);
            }
        });

        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                // Hide status bar
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                // Show status bar
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(youTubePlayerView.isFullScreen()){
                    youTubePlayerView.exitFullScreen();
                }else{
                    youTubePlayerView.release();
                    onBackPressed();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onBackPressed() {
        if(youTubePlayerView.isFullScreen()){
            youTubePlayerView.exitFullScreen();
        }else{
            youTubePlayerView.release();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void pip(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pip.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            enterPictureInPictureMode();
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        if(isInPictureInPictureMode()){
            toggleUi();
        }

    }

    private void toggleUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            layout.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onUserLeaveHint () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            pip.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            enterPictureInPictureMode();
        }
    }

}
