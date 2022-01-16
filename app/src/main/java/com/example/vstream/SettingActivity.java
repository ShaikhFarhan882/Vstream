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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseCommonRegistrar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;


public class SettingActivity extends AppCompatActivity {
    TextView change_password;
    TextView aboutApp;
    TextView yourVideos;
    FirebaseAuth mAuth;


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
        yourVideos = (TextView) findViewById(R.id.your_videos_library);
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
                showDialog();
            }
        });

        //Showing all the videos uploaded by the user
        yourVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),videosLibrary.class));

            }
        });


    }

    private void showDialog() {
        final Dialog dialog = new Dialog(SettingActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_change_password);

        //Typecasting
        final EditText currentPassword = (EditText) dialog.findViewById(R.id.dialog_current_password);
        final EditText newPassword = (EditText) dialog.findViewById(R.id.dialog_new_password);
        final Button changePwd = (Button) dialog.findViewById(R.id.update_password);
        //Update button implementation
        changePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = currentPassword.getText().toString().trim();
                String newPwd = newPassword.getText().toString().trim();

                if(TextUtils.isEmpty(oldPwd)){
                    Toasty.warning(getApplicationContext(),"Please Enter Current Password",Toasty.LENGTH_SHORT).show();
                    return;
                }
                if(newPassword.length()<8){
                    Toasty.warning(getApplicationContext(),"Password must be at least 8 characters",Toasty.LENGTH_SHORT).show();
                    return;
                }
                upadatePassword(oldPwd,newPwd,dialog);
            }
        });

        dialog.show();

    }

    private void upadatePassword(String oldPwd, String newPwd,Dialog dialog) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(),oldPwd);


        user.reauthenticate(authCredential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toasty.success(getApplicationContext(),"Password Updated Successfully",Toasty.LENGTH_SHORT).show();
                                        dialog.dismiss();

                                    } else {
                                        Toasty.success(getApplicationContext(),"Failec to update password",Toasty.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            Toasty.success(getApplicationContext(),"Failed to authenticate user",Toasty.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });


    }


}