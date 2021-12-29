package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class Dashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addVideo;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference reference;
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



       //fetching the data from the firebase using firebaseRecycler Adapter
       FirebaseRecyclerOptions<Member> options =
                new FirebaseRecyclerOptions.Builder<Member>()
                        .setQuery(reference, Member.class)
                        .build();

        FirebaseRecyclerAdapter<Member,ViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Member, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Member model) {
                holder.setExoplayer(getApplication(),model.getVideoTitle(),model.getVideoURL());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_row,parent,false);
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


    }


   //OnBack Pressed Implementation
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



    //Searching the video in the firebase database using search option
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
                holder.setExoplayer(getApplication(),model.getVideoTitle(),model.getVideoURL());
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_row,parent,false);
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





}



