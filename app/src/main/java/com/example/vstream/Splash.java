package com.example.vstream;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash extends AppCompatActivity {

    Animation topAnim,bottomAnim;
    TextView banner,slogan;
    ImageView logo;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Initializing the animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Typecasting
        logo = (ImageView)findViewById(R.id.Vstream_logo);
        banner = (TextView) findViewById(R.id.Vstream_banner);
        slogan = (TextView)findViewById(R.id.Vstream_slogan);

        //Hooking the animations
        logo.setAnimation(topAnim);
        banner.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);


        //Hide Notification Panel
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        },3000);
    }
}