package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class Dashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton addVideo;
    RecyclerView recyclerView;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //Hide Notification Panel
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        addVideo = (FloatingActionButton)findViewById(R.id.Add_video);

        //Typecasting the Recyclerview and FireDatabase
        recyclerView = (RecyclerView) findViewById(R.id.Dashboard_RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        database = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("video");



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

        //Floating action button
        addVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent = new Intent(getApplicationContext(),AddVideo.class);
              startActivity(intent);
            }
        });


        }

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

}