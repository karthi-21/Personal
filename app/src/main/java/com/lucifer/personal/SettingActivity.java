package com.lucifer.personal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialize.color.Material;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.squareup.picasso.Picasso;
import com.suke.widget.SwitchButton;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class SettingActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    Toolbar toolbar;
    CircleImageView profile;
    TextView username, mail, fav, bookmark;
    FirebaseUser user;
    RelativeLayout root;
    private DatabaseReference mDatabase, nDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        root = findViewById(R.id.root);

        toolbar =findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        username = findViewById(R.id.user_name);
        username.setText(user.getDisplayName());
        mail = findViewById(R.id.user_mail);
        mail.setText(user.getEmail());
        profile = findViewById(R.id.profile_image);
        Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.profile_image).into(profile);

        fav = findViewById(R.id.favCount);
        bookmark = findViewById(R.id.bookmarkCount);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Bookmark").child(user.getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long c = dataSnapshot.getChildrenCount();
                bookmark.setText(c.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FireBaseError", "loadPost:onCancelled", databaseError.toException());
            }
        });

        nDatabase = FirebaseDatabase.getInstance().getReference().child("Favorites").child(user.getUid());
        nDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long c = dataSnapshot.getChildrenCount();
                fav.setText(c.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FireBaseError", "loadPost:onCancelled", databaseError.toException());
            }
        });

//        SwitchButton switchButton = findViewById(R.id.nightModebutton);
//        switchButton.setChecked(true);
//        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
//                if(!isChecked){
//                    setTheme(R.style.LightText);
//                    root.setBackgroundColor(Color.WHITE);
//                    pc.setCardBackgroundColor(Color.WHITE);
//                    sc.setCardBackgroundColor(Color.WHITE);
//                }else{
//                    root.setBackgroundResource(R.color.darkbg);
//                    pc.setCardBackgroundColor(getResources().getColor(R.color.darkbg));
//                    sc.setCardBackgroundColor(getResources().getColor(R.color.darkbg));
//                }
//
//            }
//        });
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

    public void signOut(View view) {
        if (user != null) {
            Toasty.warning(SettingActivity.this, "See you soon..." + user.getDisplayName(), Toast.LENGTH_SHORT, true).show();
        }
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        finish();
    }

    public void bookmark(View view) {
        startActivity(new Intent(SettingActivity.this, BookmarkActivity.class));
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        finish();
    }

    public void favorites(View view) {
        startActivity(new Intent(SettingActivity.this, FavroitesActivity.class));
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
        finish();
    }

    public void privacy(View view) {
        Snackbar.make(view,"About to be implemented",3000).show();
    }

    public void help(View view) {
        Snackbar.make(view,"About to be implemented",3000).show();
    }

    public void about(View view) {
        startActivity(new Intent(this,AboutActivity.class));
    }

    public void appLang(View view) {
        new FancyAlertDialog.Builder(this)
                .setTitle("App Language")
                .setBackgroundColor(Color.parseColor("#392A53"))  //Don't pass R.color.colorvalue
                .setMessage("App language is by default English, you can choose your own language in future updates.")
                .setPositiveBtnBackground(Color.parseColor("#9F71EC"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("OK")
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(getApplicationContext(),"Thanks for being with us !!!",Toast.LENGTH_SHORT).show();
                    }
                }).OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(getApplicationContext(),"Thanks for being with us !!!",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }
}
