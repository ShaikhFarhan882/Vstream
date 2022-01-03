package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class Login extends AppCompatActivity {

    TextInputLayout t1,t2;
    android.widget.Button Login;
    private FirebaseAuth mAuth;
    TextView forgotPassword;

    //Regex Pattern
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hide Notification Panel
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        t1 = findViewById(R.id.Email_Login);
        t2 = findViewById(R.id.Password_Login);
        Login = findViewById(R.id.loginButton);
        forgotPassword = (TextView) findViewById(R.id.resetPassword);
        mAuth = FirebaseAuth.getInstance();


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = t1.getEditText().getText().toString();
                String password = t2.getEditText().getText().toString();

                if(checkValidationLogin(email,password)) {

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(Login.this, Dashboard.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    else {
                                        t1.getEditText().setText("");
                                        t2.getEditText().setText("");
                                        Toasty.warning(Login.this, "Login Failed", Toasty.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }

                //When Users Provide no email and password
                else if(email.isEmpty() || password.isEmpty()){
                    Toasty.error(Login.this,"Please Enter Email and Password", Toasty.LENGTH_SHORT).show();
                }

                else {
                    t1.getEditText().setText("");
                    t2.getEditText().setText("");
                    Toasty.warning(Login.this, "Invalid Email or Password", Toasty.LENGTH_SHORT).show();

                }

            }
        });


        //Forgot Password Implementation
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = t1.getEditText().getText().toString();

                mAuth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toasty.success(Login.this,"Reset Password Link Send Successfully",Toasty.LENGTH_SHORT).show();
                                }
                                else {
                                    Toasty.error(Login.this,"Failed to Send Password Reset Link",Toasty.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private boolean checkValidationLogin(String email,String password) {
        return  validate(email) &&  password.length() >= 8;
    }


}
