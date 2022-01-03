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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Button Logout;
    Button UpdateDetails;
    EditText Username;
    CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

       /* //Hide Notification Panel
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();*/

        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);

        //Typecasting
        Logout = (Button)findViewById(R.id.logout_Button);
        UpdateDetails =(Button)findViewById(R.id.update_Profile);
        Username =(EditText)findViewById(R.id.userName_Profile);
        profileImage =(CircleImageView)findViewById(R.id.userImage_profile);









        bottomNavigationView = findViewById(R.id.BottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.profile);

        //Bottom navigation bar implementation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.profile:
                        break;

                    //Dashboard=Home
                    case R.id.home:
                        Intent intent1 = new Intent(Profile.this,Dashboard.class);
                        startActivity(intent1);
                        overridePendingTransition(0,0);
                        finish();
                        break;

                    case R.id.category:
                        Intent intent2 = new Intent(Profile.this,Category.class);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        finish();
                        break;


                }
                return false;
            }
        });




        //Implementing the Logout Button
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toasty.success(getApplicationContext(),"Logged Out Successfully",Toasty.LENGTH_SHORT).show();
                startActivity(new Intent(Profile.this,MainActivity.class));
                finish();
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
                        Profile.super.onBackPressed();
                    }
                }).create().show();
    }
}