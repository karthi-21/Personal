package com.lucifer.personal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import es.dmoral.toasty.Toasty;

public class NewsDetailActivity extends AppCompatActivity implements OnInitListener{

    String author=null,title=null,description=null,urlToImage=null,content=null,datePublished=null;
    ImageView imageView;
    TextView authorText,titleText,descriptionText,contentText,publishedText;
    LottieAnimationView favorite, bookmark, share, voice, close;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabase;
    FloatingActionButton speakNews;
    Context context;
    TextToSpeech textToSpeech;
    RelativeLayout vbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("News...");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        author = intent.getStringExtra("author");
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        urlToImage = intent.getStringExtra("urlToImage");
        content = intent.getStringExtra("content");
        datePublished = intent.getStringExtra("publishedAt");

        authorText = findViewById(R.id.author);
        titleText = findViewById(R.id.title);
        descriptionText = findViewById(R.id.description);
        contentText = findViewById(R.id.content);
        publishedText = findViewById(R.id.publishedAt);
        imageView = findViewById(R.id.urlToImage);
        share = findViewById(R.id.share);
        bookmark = findViewById(R.id.bookmark);
        favorite = findViewById(R.id.favorite);
        speakNews = findViewById(R.id.speakNews);
        voice = findViewById(R.id.voice);
        vbg = findViewById(R.id.voiceBgR);
        close = findViewById(R.id.close);
        textToSpeech = new TextToSpeech(this, this);

        Picasso.get().load(urlToImage).into(imageView);
        authorText.setText(author);
        titleText.setText(title);
        descriptionText.setText(description);
        content+="\n\n\n\n";
        contentText.setText(content);
        publishedText.setText(datePublished);

        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    favorite.playAnimation();

                    News news = new News();

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Favorites").child(user.getUid()).child(title).setValue(news);

                }catch(Exception ex){
                    Toasty.error(NewsDetailActivity.this, ex.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bookmark.playAnimation();

                    News news = new News();

                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("Bookmark").child(user.getUid()).child(title).setValue(news);

                }catch(Exception ex){
                    Toasty.error(NewsDetailActivity.this, ex.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                share.playAnimation();

                Snackbar.make(v, "Preparing to share!", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                shareItem(urlToImage,title,description);
            }
        });

        speakNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String text = title+". "+description+". "+content;
                //calling the speak function to start TTS
                speak(text);

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close.playAnimation();
                textToSpeech.shutdown();
                textToSpeech.stop();
                vbg.setVisibility(View.GONE);
            }
        });

        favorite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(NewsDetailActivity.this,"Added to Favorities",Toast.LENGTH_LONG).show();
                return true;
            }
        });

        bookmark.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(NewsDetailActivity.this,"Bookmarked",Toast.LENGTH_LONG).show();
                return true;
            }
        });

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
                    i.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap, NewsDetailActivity.this));
                    startActivity(Intent.createChooser(i, "Share News"));
                }catch (Exception ex){
                    Toasty.error(NewsDetailActivity.this,ex.getMessage(),Toast.LENGTH_SHORT, true).show();
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
            bmpUri = FileProvider.getUriForFile(NewsDetailActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public void onBackPressed() {
        textToSpeech.stop();
        textToSpeech.shutdown();
        vbg.setVisibility(View.GONE);
        finish();
    }

    private void speak(final String text) {
        if(text != null) {
            final HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "total news");

            vbg.setVisibility(View.VISIBLE);
            close.playAnimation();
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    textToSpeech.shutdown();
                    textToSpeech.stop();
                    vbg.setVisibility(View.GONE);
                }

                @Override
                public void onError(String utteranceId) {
                    textToSpeech.speak("Some error occured", TextToSpeech.QUEUE_FLUSH, null);
                }
            });
        }

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
        } else {
            Log.e("NewsDetailsActivity", "TTS Initilization Failed!");
        }
    }

    public void onPause(){
        if(textToSpeech !=null || textToSpeech.isSpeaking()){
            vbg.setVisibility(View.GONE);
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {

        if(textToSpeech != null){
            textToSpeech.shutdown();
            vbg.setVisibility(View.GONE);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            vbg.setVisibility(View.GONE);
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
