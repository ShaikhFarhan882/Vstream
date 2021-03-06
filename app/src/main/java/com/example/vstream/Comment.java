package com.example.vstream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import es.dmoral.toasty.Toasty;

public class Comment extends AppCompatActivity {
    EditText CommentText;
    Button Cancel,Submit;
    DatabaseReference userReference;
    DatabaseReference commentReference;
    String postKey;
    String randomPostkey;
    String myId;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        //Setting the Action Bar Color
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Comment Section");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#3700B3"));
        actionBar.setBackgroundDrawable(colorDrawable);

        //Getting data from previous intent
        postKey = getIntent().getStringExtra("postkey");

        userReference = FirebaseDatabase.getInstance().getReference("userProfile");
        commentReference = FirebaseDatabase.getInstance().getReference().child("video").child(postKey).child("comments");

        //Getting User info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        //Typecasting
        CommentText = (EditText) findViewById(R.id.comment_box);
        Cancel = (Button) findViewById(R.id.cancel_comment);
        Submit = (Button) findViewById(R.id.submit_comment);

        recyclerView = (RecyclerView)findViewById(R.id.Comment_recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        //Submit button Implementation
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CommentText.getText().toString().isEmpty()) {
                    userReference.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Fetching the user Name and image
                            if (snapshot.exists()) {
                                String userName = snapshot.child("uName").getValue().toString();
                                String userImage = snapshot.child("uImage").getValue().toString();
                                processComment(userName, userImage);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                else {
                    Toasty.warning(getApplicationContext(),"Cannot add empty comment",Toasty.LENGTH_SHORT).show();
                }


            }




            private void processComment(String userName, String userImage){
                String submittedText = CommentText.getText().toString();
                randomPostkey = userId + "" + new Random().nextInt(1000);
                int commentId = new Random().nextInt(10000);

                //Getting the Date and Time of the comment
                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());


                //Adding the attributes to the database using the hashmap
                HashMap comment = new HashMap();
                comment.put("userId",userId);
                comment.put("userImage",userImage);
                comment.put("userName",userName);
                comment.put("userMessage",submittedText);
                comment.put("Time",currentTime);
                comment.put("Date",currentDate);
                comment.put("commentId",commentId);

                commentReference.child(randomPostkey).updateChildren(comment)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                //If comment is added successfully
                                if(task.isSuccessful()){
                                    Toasty.success(getApplicationContext(),"Added Comment Successfully",Toasty.LENGTH_SHORT).show();
                                }
                                else {
                                    Toasty.error(getApplicationContext(),"Failed to add comment",Toasty.LENGTH_SHORT).show();
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


            }

        });

        //Cancel Button Implementation
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentText.setText("");
            }
        });



    }

    //Fetching the data from firebase to recyclerView in app
    @Override
    protected void onStart() {
        super.onStart();

        //Displaying a toast message for how to delete a comment
        Toasty.normal(getApplicationContext(),"Hold on comment to delete it",Toasty.LENGTH_SHORT).show();

        FirebaseRecyclerOptions<ModelComment> options =
                new FirebaseRecyclerOptions.Builder<ModelComment>()
                        .setQuery(commentReference, ModelComment.class)
                        .build();



        FirebaseRecyclerAdapter<ModelComment,ViewHolderComment> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ModelComment, ViewHolderComment>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderComment holder, int position, @NonNull ModelComment model) {
                //Setting up the layout
                holder.userName.setText(model.getUserName());
                holder.userMessage.setText(model.getUserMessage());
                holder.dateTime.setText("Date:"+ model.getDate() + " " +"Time:" + model.getTime());
                Glide.with(holder.userImage.getContext()).load(model.getUserImage()).into(holder.userImage);

                //Setting up the delete interface for comments
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userid = user.getUid();
                myId = getItem(position).getUserId();
                final String rPostkey = getRef(position).getKey();


                holder.setOnClickListener(new ViewHolderComment.ClickListener() {
                    @Override
                    public void onItemLongClick(View view, int position) {
                       if(myId.equals(userid)){
                           showDeleteDialog(myId,rPostkey);
                       }
                       else{
                           Toasty.error(getApplicationContext(),"Cannot Delete Others Comment",Toasty.LENGTH_SHORT).show();
                       }
                    }
                });





            }

            @NonNull
            @Override
            public ViewHolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_comment,parent,false);
                return new ViewHolderComment(view);
            }
        };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);




    }

    private void showDeleteDialog(String myId,String rPostkey) {

        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure to delete this comment?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {

                        deleteComment(myId,rPostkey);

                    }
                }).create().show();


    }

    private void deleteComment(String myId,String rPostkey){

        DatabaseReference ref = commentReference;
        ref.child(rPostkey).removeValue();
        Toasty.success(getApplicationContext(),"Successfully Deleted Comment",Toasty.LENGTH_SHORT).show();

    }


}



