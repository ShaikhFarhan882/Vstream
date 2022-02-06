package com.example.vstream;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class Profile extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Button Logout;
    Button UpdateButton;
    EditText Username;
    CircleImageView profileImage;
    TextView userEmail;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    Uri filepath;
    Bitmap bitmap;
    String userID="";
    String lastValue = "";

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
        actionBar.setTitle("Profile");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);

        //Typecasting
        Logout = (Button)findViewById(R.id.logout_Button);
        UpdateButton =(Button)findViewById(R.id.update_Profile);
        Username =(EditText)findViewById(R.id.userName_Profile);
        profileImage =(CircleImageView)findViewById(R.id.userImage_profile);
        userEmail =(TextView)findViewById(R.id.emailtv_Profle);
        databaseReference = FirebaseDatabase.getInstance().getReference("userProfile");
        storageReference = FirebaseStorage.getInstance().getReference("uploadedProfileImages");
        lastValue = Username.getText().toString();


        //Fetching the current user to store the image and name related to it.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();


        //Setting the onclick listener on Profile image placeholder
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext()).
                        withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //Selecting the image from the device storage
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Please select file"),101);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
            }
        });



        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(profileImage.getDrawable()!=null && !(Username.getText().toString().isEmpty()) && lastValue != (Username.getText().toString()) ){
                    updateToFirebase();
                }
                else {
                    Toasty.error(getApplicationContext(),"Please Add Image and Username",Toasty.LENGTH_SHORT).show();
                }


            }
        });




        //Bottom Navigation Bar Typecasting

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


        //Displaying the user Email in the profile section and showing the toast message related to it.
        String email_tv= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userEmail.setText(email_tv);

        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toasty.info(getApplicationContext(),"This is your current email address",Toasty.LENGTH_SHORT).show();
            }
        });

        //Checking if the user is connected to internet or not with isOnline function
        if (!isOnline()){
            Toasty.error(getApplicationContext(), "Network connection is not Available", Toasty.LENGTH_SHORT).show();
            Toasty.error(getApplicationContext(), "Unable to View/Update Profile", Toasty.LENGTH_SHORT).show();
        }



         // Updating the user Image and Name
    /*    FirebaseUser usere = FirebaseAuth.getInstance().getCurrentUser();
        userID = usere.getUid();

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //Getting the name form database
                    Username.setText(snapshot.child("uName").getValue().toString());
                    //Getting the ImageUrl from database
                    String imageUrl = (snapshot.child("uImage").getValue().toString());

                    //Using the glide to display image from URL
                    Glide.with(getApplicationContext()).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101 && resultCode==RESULT_OK && data!=null){
            filepath = data.getData();

                //Setting the image on placeholder
                try{
                    InputStream inputStream = getContentResolver().openInputStream(filepath);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    profileImage.setImageBitmap(bitmap);


                }
                catch (Exception ex){
                    Toasty.error(getApplicationContext(),ex.getMessage(),Toasty.LENGTH_SHORT).show();

                }
        }
    }

    public void updateToFirebase(){
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Uploading");
        pd.show();

        final StorageReference uploader=storageReference.child("profileimages/"+"img"+System.currentTimeMillis());
        if(filepath!=null) {

            uploader.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final Map<String, Object> map = new HashMap<>();
                                    map.put("uImage", uri.toString());
                                    map.put("uName", Username.getText().toString());

                                    Log.e("Database Ref Snapshot",databaseReference.child(userID).toString());

                                    //If user Does not exists setValue
                                    if(databaseReference.child(userID)==null){
                                        databaseReference.child(userID).setValue(map);
                                    }
                                    //if user exists Update the Value
                                    else {
                                        databaseReference.child(userID).updateChildren(map);
                                    }

                                    pd.dismiss();
                                    Toasty.success(getApplicationContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            //Calculating the uploaded percentage
                            float percent = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                            pd.setMessage("Uploaded :" + (int) percent + "%");
                        }
                    });
        }

        else {
            final Map<String, Object> map = new HashMap<>();
            map.put("uName", Username.getText().toString());

            databaseReference.child(userID).updateChildren(map);

            pd.dismiss();
            Toasty.success(getApplicationContext(), "Updated Name Successfully", Toast.LENGTH_SHORT).show();

        }
    }

    //Displaying the profile image and Name if user has already uploaded it in the app.
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //Getting the name form database
                    Username.setText(snapshot.child("uName").getValue().toString());
                    //Getting the ImageUrl from database
                    String imageUrl = (snapshot.child("uImage").getValue().toString());

                    //Using the glide to display image from URL
                    Glide.with(getApplicationContext()).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).into(profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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



    //Checking if the application is connected to the internet or not
    boolean status;
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }




}

