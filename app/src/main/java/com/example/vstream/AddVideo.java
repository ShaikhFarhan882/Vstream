package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import es.dmoral.toasty.Toasty;

public class AddVideo extends AppCompatActivity {

    VideoView videoView;
    Button Browse,Upload;
    Uri videouri;
    MediaController mediaController;
    EditText videoTitle;

    //Radio Button
    RadioGroup radioGroup;
    RadioButton radioButton;
    int checkedRadioID;

    //Firebase Storage and realtime database reference
    StorageReference storageReference;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        //Hide Notification Panel
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        //Type Casting
        videoView = (VideoView)findViewById(R.id.videoView);
        Browse = (Button)findViewById(R.id.browse_video);
        Upload = (Button) findViewById(R.id.upload_video);
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        videoTitle = (EditText)findViewById(R.id.Video_title);


        radioGroup = (RadioGroup) findViewById(R.id.category_radiogroup);

        //Storage and Realtime database Initializing
        storageReference = FirebaseStorage.getInstance().getReference("uploadedVideo");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");



       //Browse Button Implementation and Managing Runtime Permissions

        Browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                //selecting video from Storage
                                Intent intent = new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,101);

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


        //Upload button Implementation

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProcess();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101 && resultCode==RESULT_OK){
            videouri=data.getData();
            videoView.setVideoURI(videouri);
        }
    }

    //Radio Button Implementation
    public void Checkbutton (View v){

        checkedRadioID = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(checkedRadioID);

        Toasty.normal(getApplicationContext(),"Selected Category: " + radioButton.getText(),Toasty.LENGTH_SHORT).show();

    }


    //Getting the video Extension
    public String getExtension(Uri videouri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(videouri));

    }


    //Uploading the actual video
    public void uploadProcess(){

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading");
        dialog.show();

       final StorageReference uploader = storageReference.child(System.currentTimeMillis() + "." + getExtension(videouri));


        uploader.putFile(videouri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Realtime database
                                Member obj = new Member(videoTitle.getText().toString(),uri.toString());
                                databaseReference.child(databaseReference.push().getKey()).setValue(obj)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                dialog.dismiss();
                                                Toasty.success(getApplicationContext(),"Successfully Uploaded",Toasty.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                dialog.dismiss();
                                                Toasty.success(getApplicationContext(),"Failed to Upload",Toasty.LENGTH_SHORT).show();

                                            }
                                        });

                            }
                        });

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        //calculating the uploaded percentage
                        float per=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded :"+(int)per+"%");

                    }
                });

    }
}