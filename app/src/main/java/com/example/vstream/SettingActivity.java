package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseCommonRegistrar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;


public class SettingActivity extends AppCompatActivity {
    TextView change_password;
    TextView aboutApp;
    FirebaseAuth mAuth;
    EditText currentPassword;
    EditText newPassword;
    Button updatePwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);


        change_password = (TextView) findViewById(R.id.change_pwd);
        aboutApp = (TextView) findViewById(R.id.about_app);
        mAuth = FirebaseAuth.getInstance();




       //About the app
       aboutApp.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(getApplicationContext(),AboutApp.class));

           }
       });

        //Changing the users password through their Register email
        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.success(getApplicationContext(),"Chala",Toasty.LENGTH_SHORT).show();

            }
        });


    }


}