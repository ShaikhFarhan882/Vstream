package com.example.vstream;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AboutApp extends AppCompatActivity {

    Animation topAnim,bottomAnim;
    TextView aboutText;
    TextView devName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("About the App");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);

        //Initializing the animation
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Typecasting
        aboutText = (TextView) findViewById(R.id.about_app_tv);
        devName = (TextView) findViewById(R.id.dev_name);

        //Hooking the animation
        aboutText.setAnimation(topAnim);
        devName.setAnimation(bottomAnim);



    }
}