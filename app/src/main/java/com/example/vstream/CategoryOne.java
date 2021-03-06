package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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

public class CategoryOne extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    DatabaseReference likeReference;
    Boolean likeStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_one);

        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Sports");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);

        recyclerView = (RecyclerView)findViewById(R.id.catOne_RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        databaseReference = FirebaseDatabase.getInstance().getReference("video");
        likeReference = FirebaseDatabase.getInstance().getReference("likes");




        //Fetching the Video based on the category

        String query = "Sports";
        Query firebaseQuery = databaseReference.orderByChild("videoCategory").startAt(query).endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<Member> options =
                new FirebaseRecyclerOptions.Builder<Member>()
                        .setQuery(firebaseQuery, Member.class)
                        .build();

        FirebaseRecyclerAdapter<Member,ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Member, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Member model) {
                //Getting the current user ID and VideoID for like functionality
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserID = user.getUid();
                final String postKey = getRef(position).getKey();
                holder.getLikeButtonStatus(postKey,currentUserID);

                //getting VideoUrl for shareButton
                final String shareVideoUrl = getItem(position).getVideoURL();

                //If user clicks on the like button;
                holder.like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likeStatus = true;

                        likeReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(likeStatus==true){

                                    if(snapshot.child(postKey).hasChild(currentUserID)){
                                        likeReference.child(postKey).child(currentUserID).removeValue();
                                        likeStatus = false;
                                    }
                                    else{
                                        //Like of user is recorded.
                                        likeReference.child(postKey).child(currentUserID).setValue(true);
                                        Toasty.success(getApplicationContext(),"Liked",Toasty.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(getApplicationContext(),Comment.class);
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

                //Hiding the delete Button
                holder.delete_btn.setVisibility(View.INVISIBLE);

                //Setting up the exoplayer
                holder.setExoplayer(getApplication(),model.getVideoTitle(),model.getVideoURL());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_row_dashboard,parent,false);
                return new ViewHolder(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


        //Checking if the user is connected to internet or not with isOnline function
        if (!isOnline()){
            Toasty.error(getApplicationContext(), "Network connection is not Available", Toasty.LENGTH_SHORT).show();
            Toasty.error(getApplicationContext(), "Unable to Load Videos", Toasty.LENGTH_SHORT).show();
        }


        //checking if the data exists or not in the firebase
        Query dataChecker = databaseReference.orderByChild("videoCategory").equalTo(query);

        dataChecker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toasty.success(getApplicationContext(),"Videos Found",Toasty.LENGTH_SHORT).show();
                }
                else{
                    Toasty.error(getApplicationContext(),"No Videos Found",Toasty.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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



