package com.lucifer.personal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.glide.slider.library.Animations.DescriptionAnimation;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.SliderTypes.BaseSliderView;
import com.glide.slider.library.SliderTypes.TextSliderView;
import com.glide.slider.library.Tricks.ViewPagerEx;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import am.appwise.components.ni.NoInternetDialog;

public class LiveTVActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{

    BoomMenuButton bmbTamil, bmbTelugu, bmbEnglish, bmbMalayalam, bmbKannada, bmbHindi;
    private SliderLayout mDemoSlider;

    String[] tamilCh = {
            "News7",
            "News18",
            "Sathyam",
            "Polimer",
            "Sun News",
            "PuthiyaThalaimurai"
    };
    int[] tamilDr = {R.drawable.news7,R.drawable.news18,R.drawable.sathyam,R.drawable.polimer,R.drawable.sunnews,R.drawable.puthiyathalaimurai};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tv);

        NoInternetDialog noInternetDialog = new NoInternetDialog.Builder(this).build();

        requestStoragePermission();

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Live News");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //INITIALIZATION
        bmbTamil = findViewById(R.id.bmbTamil);
        bmbTelugu = findViewById(R.id.bmbTelugu);
        bmbEnglish = findViewById(R.id.bmbEnglish);
        bmbMalayalam = findViewById(R.id.bmbMalayalam);
        bmbKannada = findViewById(R.id.bmbKannada);
        bmbHindi = findViewById(R.id.bmbHindi);
        //slider initialization
        mDemoSlider = findViewById(R.id.slider);

        ArrayList<String> listUrl = new ArrayList<>();
        ArrayList<String> listName = new ArrayList<>();

        listUrl.add("https://i.ytimg.com/vi/JuCRL2UYLVo/maxresdefault.jpg");
        listName.add("News7");

        listUrl.add("https://images.news18.com/static_news18/pix/ibnhome/news18/FireTv/firetv-CNNIBN.png");
        listName.add("CNN News18");

        listUrl.add("https://i.ytimg.com/vi/LXFxoS9ZJGg/maxresdefault.jpg");
        listName.add("Puthiya Thalaimurai");

        listUrl.add("https://i.ytimg.com/vi/P2FlyhNlyE0/maxresdefault.jpg");
        listName.add("Polimer News");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(this);
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
                    .description(listName.get(i))
                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putString("extra", listName.get(i));
            mDemoSlider.addSlider(sliderView);
        }

        // set Slider Transition Animation
        // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);


        //ALL FOR LOOPS ARE FOR PROVIDING CHANNELS IN BOOM MENU
        for (int i = 0; i < bmbTamil.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(tamilDr[i])
                    .normalText(tamilCh[i])
                    .rippleEffect(true)
                    .shadowEffect(true)
                    .listener(new OnBMClickListener() {
                        @Override
                        public void onBoomButtonClick(int index) {
                            if(index==0){
                                Toast.makeText(LiveTVActivity.this, "News7 " + index, Toast.LENGTH_SHORT).show();
                                Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
                                detailIntent.putExtra("videoId", "65KzlLiKjEg");
                                startActivity(detailIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }else if(index==1){
                                Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
                                detailIntent.putExtra("videoId", "GegB0Vp7D-c");
                                startActivity(detailIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }else if(index==2){
                                Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
                                detailIntent.putExtra("videoId", "1JreFKwJLfk");
                                startActivity(detailIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }else if(index==3){
                                Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
                                detailIntent.putExtra("videoId", "ntwdBX-PZpA");
                                startActivity(detailIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }else if(index==4){
                                Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
                                detailIntent.putExtra("videoId", "djP1koLK0f8");
                                startActivity(detailIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }else if(index==5){
                                Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
                                detailIntent.putExtra("videoId", "jIdgzQHDGIs");
                                startActivity(detailIntent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }
                    });
            bmbTamil.addBuilder(builder);
        }

        for (int i = 0; i < bmbTelugu.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_bookmark)
                    .normalText("Butter Doesn't fly!");
            bmbTelugu.addBuilder(builder);
        }

        for (int i = 0; i < bmbEnglish.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_bookmark)
                    .normalText("Butter Doesn't fly!");
            bmbEnglish.addBuilder(builder);
        }

        for (int i = 0; i < bmbMalayalam.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_bookmark)
                    .normalText("Butter Doesn't fly!");
            bmbMalayalam.addBuilder(builder);
        }

        for (int i = 0; i < bmbKannada.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_bookmark)
                    .normalText("Butter Doesn't fly!");
            bmbKannada.addBuilder(builder);
        }

        for (int i = 0; i < bmbHindi.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder()
                    .normalImageRes(R.drawable.ic_bookmark)
                    .normalText("Butter Doesn't fly!");
            bmbHindi.addBuilder(builder);
        }

    }

    @Override
    protected void onStart() {
        mDemoSlider.startAutoCycle();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        if(slider.getBundle().get("extra") == "News7"){
            Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
            detailIntent.putExtra("videoId", "65KzlLiKjEg");
            startActivity(detailIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if(slider.getBundle().get("extra") == "CNN News18"){
            Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
            detailIntent.putExtra("videoId", "Gy20j68_hgs");
            startActivity(detailIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }else if(slider.getBundle().get("extra") == "Puthiya Thalaimurai"){
            Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
            detailIntent.putExtra("videoId", "ntwdBX-PZpA");
            startActivity(detailIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if(slider.getBundle().get("extra") == "Polimer News"){
            Intent detailIntent = new Intent(LiveTVActivity.this, VideoPlayActivity.class);
            detailIntent.putExtra("videoId", "EAZVxt9_urk");
            startActivity(detailIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

    private void requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            //Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        //Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LiveTVActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
