package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

public class Dashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addVideo;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference likeReference;
    Boolean likeStatus = false;

    String searchtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Hide Notification Panel
       /* getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();*/

        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);

        addVideo = (FloatingActionButton)findViewById(R.id.Add_video);

        //Typecasting the Recyclerview and FireDatabase
        recyclerView = (RecyclerView) findViewById(R.id.Dashboard_RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("video");

        //Typecasting the likeReference
        likeReference = FirebaseDatabase.getInstance().getReference("likes");




       //fetching the data from the firebase using firebaseRecycler Adapter
        FirebaseRecyclerOptions<Member> options =
                new FirebaseRecyclerOptions.Builder<Member>()
                        .setQuery(reference, Member.class)
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


                //Setting the exoplayer
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




       //Working with the bottom Navigation Bar

        bottomNavigationView = findViewById(R.id.BottomNavigation);

        bottomNavigationView.setSelectedItemId(R.id.home);

        //Navigation bar Implementation

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                   //Dashboard=Home
                    case R.id.home:
                        break;

                    case R.id.category:
                        Intent intent1 = new Intent(Dashboard.this,Category.class);
                        startActivity(intent1);
                        overridePendingTransition(0,0);
                        finish();
                        break;

                    case R.id.profile:
                        Intent intent2 = new Intent(Dashboard.this,Profile.class);
                        startActivity(intent2);
                        overridePendingTransition(0,0);
                        finish();
                        break;


                }
                return false;
            }
        });


        //Floating action button to add videos
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(),AddVideo.class);
              startActivity(intent);
            }
        });


        //Checking if the user is connected to internet or not with isOnline function.
        if (!isOnline()){
            Toasty.error(getApplicationContext(), "Network connection is not Available", Toasty.LENGTH_SHORT).show();
            Toasty.error(getApplicationContext(), "Unable to Load Videos", Toasty.LENGTH_SHORT).show();
        }



    }
    //end of onCreate()




    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Dashboard.super.onBackPressed();
                    }
                }).create().show();
    }




    //Searching the video in the firebase database using search icon.
    private void firebaseSearch(String searchtext){

        String query = searchtext;
        Query firebaseQuery = reference.orderByChild("videoTitle").startAt(query).endAt(query + "\uf8ff");

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

               //setting up the exoplayer
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
    }







  //Related to search Option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.Search_firebase);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                Toasty.success(getApplicationContext(),"Video Found Successfully",Toasty.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    //Selecting options in menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings_app:
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));

        }
        return super.onOptionsItemSelected(item);
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



