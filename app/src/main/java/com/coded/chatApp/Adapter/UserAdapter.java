package com.coded.chatApp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coded.chatApp.MessageActivity;
import com.coded.chatApp.Models.User;
import com.coded.chatApp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;

    public UserAdapter(Context context, List<User> users, Boolean ischat) {
        this.mContext = context;
        this.mUsers = users;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());
        if (user.getImageurl().equals("default")) {
            viewHolder.profilepic.setImageResource(R.drawable.mydp);
        } else {
            Glide.with(mContext).load(user.getImageurl()).into(viewHolder.profilepic);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumptomessage = new Intent(mContext, MessageActivity.class);
                jumptomessage.putExtra("Userid", user.getId());
                mContext.startActivity(jumptomessage);
            }
        });

        if(ischat==true) {
            viewHolder.image_on.setVisibility(View.VISIBLE);
            if (user.getStatus().equals("online")) {

                viewHolder.image_on.setBorderColor(Color.GREEN);
            } else {

                viewHolder.image_on.setBorderColor(Color.GRAY);
            }
        }

    }
    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView profilepic;
        public TextView username;
        public CircleImageView image_on;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.status);
            profilepic = itemView.findViewById(R.id.profile_pic);
            image_on = itemView.findViewById(R.id.image_on);
//            image_off= itemView.findViewById(R.id.image_off);

        }
    }

}
