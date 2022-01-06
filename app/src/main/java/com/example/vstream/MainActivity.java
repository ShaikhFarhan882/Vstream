package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class MainActivity extends AppCompatActivity {

    android.widget.Button Register;
    TextInputLayout t1,t2;
    TextView already_user;
    private FirebaseAuth mAuth;

    //Regex Pattern
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hide Notification Panel
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        Register = findViewById(R.id.registerButton);
        t1 = findViewById(R.id.Email_Register);
        t2 = findViewById(R.id.Password_Register);
        already_user =(TextView)findViewById(R.id.existingUser);


        already_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Login.class);
                startActivity(intent);
                finish();

            }
        });


        //Register Button Implementation

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = t1.getEditText().getText().toString();
                String password = t2.getEditText().getText().toString();

                //Performing Validations on email and password

                if(checkValidationRegister(email,password)) {

                    //Initializing Firebase
                    mAuth = FirebaseAuth.getInstance();

                    //Creating users with firebase
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        t1.getEditText().setText("");
                                        t2.getEditText().setText("");
                                        Toasty.success(MainActivity.this, "Account Created Successfully", Toasty.LENGTH_SHORT).show();
                                        Toasty.success(MainActivity.this,"Please Login to continue",Toasty.LENGTH_SHORT).show();
                                    } else {
                                        t1.getEditText().setText("");
                                        t2.getEditText().setText("");
                                        Toasty.error(MainActivity.this, "Registration Failed Existing Email Address", Toasty.LENGTH_SHORT).show();


                                    }
                                }
                            });

                }

                else if(email.isEmpty() || password.isEmpty()){
                    Toasty.error(MainActivity.this,"Please Enter Email and Password", Toasty.LENGTH_SHORT).show();
                }

                else {
                    t1.getEditText().setText("");
                    t2.getEditText().setText("");
                    Toasty.warning(MainActivity.this, "Invalid Email or Password", Toasty.LENGTH_SHORT).show();
                }

            }
        });


        // If the user is already Logged in the application
        FirebaseUser User = FirebaseAuth.getInstance().getCurrentUser();
        if(User!=null){
            Intent intent = new Intent(MainActivity.this,Dashboard.class);
            startActivity(intent);
            finish();
        }




    }




    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private boolean checkValidationRegister(String email,String password) {
      return  validate(email) &&  password.length() >= 8;
    }
}