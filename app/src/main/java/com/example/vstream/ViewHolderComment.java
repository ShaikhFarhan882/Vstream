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

        //interface Listener
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.onItemLongClick(view,getAdapterPosition());
                return false;
            }
        });
    }

    //Creating a interface in comment for delete purpose
    private ViewHolderComment.ClickListener mClickListener;

    public interface ClickListener{
        void onItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderComment.ClickListener clickListener){
        mClickListener = clickListener;
    }

}

