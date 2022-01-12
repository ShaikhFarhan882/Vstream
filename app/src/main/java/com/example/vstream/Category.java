package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import es.dmoral.toasty.Toasty;

public class Category extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Button sports, food, travel, others;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        /*//Hide Notification Panel
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();*/

        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Category");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);


        //Typecasting
        sports = (Button)findViewById(R.id.sports_category);
        food = (Button)findViewById(R.id.food_category);
        travel = (Button)findViewById(R.id.travel_category);
        others = (Button)findViewById(R.id.other_category);

        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Category.this,CategoryOne.class);
                Toasty.success(getApplicationContext(),"Related Results",Toasty.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Category.this,CategoryTwo.class);
                Toasty.success(getApplicationContext(),"Related Results",Toasty.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });

        travel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Category.this,CategoryThree.class);
                Toasty.success(getApplicationContext(),"Related Results",Toasty.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Category.this,CategoryFour.class);
                Toasty.success(getApplicationContext(),"Related Results",Toasty.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });




        bottomNavigationView = findViewById(R.id.BottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.category);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.category:
                        break;

                    case R.id.home:
                        Intent intent1 = new Intent(Category.this, Dashboard.class);
                        startActivity(intent1);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                    case R.id.profile:
                        Intent intent2 = new Intent(Category.this, Profile.class);
                        startActivity(intent2);
                        overridePendingTransition(0, 0);
                        finish();
                        break;

                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Category.super.onBackPressed();
                    }
                }).create().show();
    }

}

