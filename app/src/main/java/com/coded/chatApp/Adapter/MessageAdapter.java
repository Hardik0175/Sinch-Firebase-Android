package com.coded.chatApp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coded.chatApp.Models.Chat;
import com.coded.chatApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static int MSG_TYPE_LEFT=0;
    public static int MSG_TYPE_RIGHT=1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;

    FirebaseUser fuser;

    public  MessageAdapter(Context context, List<Chat> chat, String imageurl)
    {
        this.mContext = context;
        this.mChat = chat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        if(i == MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, int i) {
        Chat chat= mChat.get(i);

        viewHolder.showmessage.setText(chat.getMessage());

        if(imageurl.equals("default")) {
            viewHolder.profilepic.setImageResource(R.drawable.mydp);
        }
        else {
            Glide.with(mContext).load(imageurl).into(viewHolder.profilepic);
        }

        if(i == mChat.size()-1)
        {
            if(chat.isIsseen())
            {
                viewHolder.isseen.setText("Seen");
            }
            else
            {
                viewHolder.isseen.setText("Delivered");
            }
        }
        else
        {
            viewHolder.isseen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView profilepic;
        public TextView showmessage;
        public TextView isseen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showmessage=itemView.findViewById(R.id.show_message);
            profilepic=itemView.findViewById(R.id.profile_pic);
            isseen=itemView.findViewById(R.id.txt_seen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }

    }

}
