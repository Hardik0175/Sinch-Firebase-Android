package com.coded.chatApp.fragments;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.coded.chatApp.Adapter.UserAdapter;
import com.coded.chatApp.Database.Database;
import com.coded.chatApp.MainActivity;
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


public class ContactsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;
    FirebaseUser firebaseUser;

    private List<String> Firenumbers;
    private List<String> Localnumbers;

    Database database;

    boolean flag = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = new Database(getActivity(),null,null,1);

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers = new ArrayList<>();

        new loadthelist().execute("");

        updatetoken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void updatetoken(String token) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        databaseReference.child(firebaseUser.getUid()).setValue(token1);

    }

    private void readUsersDatabase(){
        mUsers.clear();
        Cursor cursor = database.viewContactUser();
        if(cursor.getCount()!=0) {
            while ((cursor.moveToNext())) {
                User user = new User(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                mUsers.add(user);
                userAdapter = new UserAdapter(getActivity(), mUsers, false);
                recyclerView.setAdapter(userAdapter);
            }
        }
    }

    public void readusers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                database.clearContactUsertable();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert firebaseUser != null;
                    assert user != null;

                    if (!user.getId().equals(firebaseUser.getUid())) {

                        if(MainActivity.arrayList_Android_Contacts.contains(user.getPhonenumber()))
                        {
                            database.addContactsUser(user);
                            mUsers.add(user);
                            //Log.e("ContactsFragment Users:", user.getPhonenumber());
                        }
                    }
                }
                userAdapter = new UserAdapter(getActivity(), mUsers, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class loadthelist extends AsyncTask<String,Void,String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            readUsersDatabase();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(MainActivity.isloaded==false) {
                while (MainActivity.isloaded == false) {

                }
                return "";
            }
            else {
                return null;
            }
        }
        @Override
        protected void onPostExecute(String s) {
            readusers();
            Toast.makeText(getActivity(),"Contacts are updated",Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
        }
    }
}