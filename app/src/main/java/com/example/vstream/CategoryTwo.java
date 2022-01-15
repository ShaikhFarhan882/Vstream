package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class CategoryTwo extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    DatabaseReference likeReference;
    Boolean likeStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_two);

        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Food");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);

        recyclerView = (RecyclerView)findViewById(R.id.catTwo_RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        databaseReference = FirebaseDatabase.getInstance().getReference("video");
        likeReference = FirebaseDatabase.getInstance().getReference("likes");

        //Fetching the Video Based on category
        String query = "Food";
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
    }
}