package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class videosLibrary extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    DatabaseReference likeReference;
    Boolean likeStatus = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos_library);

        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Your Uploaded Videos");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);

        recyclerView = (RecyclerView) findViewById(R.id.videosLibrary_RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        databaseReference = FirebaseDatabase.getInstance().getReference("video");
        likeReference = FirebaseDatabase.getInstance().getReference("likes");


        //Fetching the Video based on the userID

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();

        String query = userID;

        Query firebaseQuery = databaseReference.orderByChild("currentUserId").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<Member> options =
                new FirebaseRecyclerOptions.Builder<Member>()
                        .setQuery(firebaseQuery, Member.class)
                        .build();

        FirebaseRecyclerAdapter<Member, ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Member, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Member model) {
                //Getting the current user ID and VideoID for like functionality
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();
                final String postKey = getRef(position).getKey();
                holder.getLikeButtonStatus(postKey, currentUserID);

                //getting VideoUrl for shareButton
                final String shareVideoUrl = getItem(position).getVideoURL();

                //getting VideoTitle for deleting purpose
                final String videoName = getItem(position).getVideoTitle();

                //If user clicks on the like button;
                holder.like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeStatus = true;

                        likeReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (likeStatus == true) {

                                    if (snapshot.child(postKey).hasChild(currentUserID)) {
                                        likeReference.child(postKey).child(currentUserID).removeValue();
                                        likeStatus = false;
                                    } else {
                                        //Like of user is recorded.
                                        likeReference.child(postKey).child(currentUserID).setValue(true);
                                        Toasty.success(getApplicationContext(), "Liked", Toasty.LENGTH_SHORT).show();
                                        likeStatus = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });

                //If user clicks on the comment button
                holder.comment_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), Comment.class);
                        intent.putExtra("postkey", postKey);
                        startActivity(intent);

                    }
                });


                //If users clicks on the share Button
                holder.share_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareVideoUrl);
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, "Choose one"));
                    }
                });

                holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog(videoName);
                    }
                });


                //Setting up the exoplayer
                holder.setExoplayer(getApplication(), model.getVideoTitle(), model.getVideoURL());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_row_dashboard, parent, false);
                return new ViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


        //Checking if the user is connected to internet or not with isOnline function
        if (!isOnline()) {
            Toasty.error(getApplicationContext(), "Network connection is not Available", Toasty.LENGTH_SHORT).show();
            Toasty.error(getApplicationContext(), "Unable to View Videos library", Toasty.LENGTH_SHORT).show();
        }


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

    //Deleting the video using the Dialog
    private void showDeleteDialog(String videoName) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure to delete this video?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        Query query = databaseReference.orderByChild("videoTitle").equalTo(videoName);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                    dataSnapshot1.getRef().removeValue();
                                    Toasty.success(getApplicationContext(),"Video deleted successfully",Toasty.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }).create().show();

    }


}