package com.coded.chatApp.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coded.chatApp.Adapter.UserAdapter;
import com.coded.chatApp.Database.Database;
import com.coded.chatApp.Models.Chat;
import com.coded.chatApp.Models.User;
import com.coded.chatApp.Models.Token;
import com.coded.chatApp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {


    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    ArrayList<String> userlist;

    Database database;

    /**
     * flag For updating when connected to the internet
     */
    boolean flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        userlist = new ArrayList<>();

        database = new Database(getActivity(),null,null,1);

        reference = FirebaseDatabase.getInstance().getReference("Chats");

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    flag = true;
                    userlist.clear();
                    database.clearChatTable();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Chat chat = snapshot.getValue(Chat.class);
                        database.addChats(chat,"Delivered");
                        if(chat.getSender().equals(firebaseUser.getUid()))
                        {
                            userlist.add(chat.getReceiver());
                        }
                        if(chat.getReceiver().equals(firebaseUser.getUid()))
                        {
                            userlist.add(chat.getSender());
                        }
                    }
                    readusers();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                 //   Log.e("Error",databaseError.getMessage());
                }
            });

            if(flag!=true)
            {
                readUsersDatabase();
            }

        updatetoken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void updatetoken(String token){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1=new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);

    }

    private void readUsersDatabase() {
        mUsers.clear();
        Cursor cursor = database.viewUser();
        if(cursor.getCount()!=0) {
            while ((cursor.moveToNext())) {
                User user = new User(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                mUsers.add(user);
                userAdapter = new UserAdapter(getActivity(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }
        }
    }

    public void readusers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                database.clearUserTable();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert firebaseUser != null;
                    assert user != null;

                    if (!user.getId().equals(firebaseUser.getUid())) {
                        if(userlist.contains(user.getId()))
                        {
                            database.addUsers(user);
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getActivity(), mUsers, true);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
