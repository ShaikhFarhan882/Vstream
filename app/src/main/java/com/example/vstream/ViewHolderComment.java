package com.example.vstream;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolderComment extends RecyclerView.ViewHolder {

    CircleImageView userImage;
    TextView userName, userMessage;
    TextView dateTime;
    public ViewHolderComment(@NonNull View itemView) {
        super(itemView);

        userImage = itemView.findViewById(R.id.comment_image);
        userName = itemView.findViewById(R.id.comment_username);
        userMessage = itemView.findViewById(R.id.comment_message);
        dateTime = itemView.findViewById(R.id.comment_dateTime);
    }


}

