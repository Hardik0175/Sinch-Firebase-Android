package com.coded.chatApp;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coded.chatApp.Adapter.MessageAdapter;
import com.coded.chatApp.Database.Database;
import com.coded.chatApp.Models.Chat;
import com.coded.chatApp.Models.Client;
import com.coded.chatApp.Models.ConnectionStateMonitor;
import com.coded.chatApp.Models.User;
import com.coded.chatApp.Notifications.Data;
import com.coded.chatApp.Notifications.MyResponse;
import com.coded.chatApp.Notifications.Sender;
import com.coded.chatApp.Models.Token;
import com.coded.chatApp.SinchCall.BaseActivity;
import com.coded.chatApp.SinchCall.SinchCallListener;
import com.coded.chatApp.SinchCall.SinchService;
import com.coded.chatApp.fragments.APIService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends BaseActivity {

    CircleImageView profilepic;

    Toolbar bar;
    TextView username;
    EditText message;
    ImageButton send;


    String userid;

    FirebaseUser fuser;
    DatabaseReference reference;

    SinchClient sinchClient;
    com.sinch.android.rtc.calling.Call call;

    SinchCallListener sinchCallListener;

    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    List<Chat> mChat;

    ValueEventListener seenlistener;

    Intent intent;

    APIService apiService;

    boolean notify = false;

    boolean flag = false;

    Database database;

    private static final String TAG = "MessageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        bar =  findViewById(R.id.mybar);
        username = findViewById(R.id.status);
        profilepic = findViewById(R.id.mydp);
        message = findViewById(R.id.text_sent);
        send = findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        database = new Database(MessageActivity.this,null,null,1);

        apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        setSupportActionBar(bar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MessageActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        intent = getIntent();
        userid=intent.getStringExtra("Userid");

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        mChat = new ArrayList<>();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String message_text= message.getText().toString();
                if(!message_text.equals(""))
                {
                    Chat chat = new Chat(fuser.getUid(),userid,message_text,false);
                    database.addChats(chat,"Queue");
                    callfromdatabase();
                    sendmessage(fuser.getUid(),userid,message_text);
                }
                else
                {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_LONG).show();
                }
                message.setText("");
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if(user.getImageurl().equals("default"))
                {
                    profilepic.setImageResource(R.drawable.mydp);
                }
                else
                {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(profilepic);
                }

                readmessage(fuser.getUid(),userid,user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        seenmessage(userid);


        if(flag==false)
        {
            callfromdatabase();
        }

    }

    private void seenmessage(final String userid)
    {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenlistener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid))
                    {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Send the message to the receiver
     * @see #readmessage(String, String, String) for updatin the UI
     * @see #sendNotification(String, String, String)
     * @param sender
     * @param receiver
     * @param message
     */
    private void sendmessage(final String sender, final String receiver, String message) {

      if (ConnectionStateMonitor.connected == true) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", message);
            hashMap.put("isseen", false);

            reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("MessageActivity", "Registered");
                }
            });
      }


        final String msg =  message;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                if(notify) {
                    sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void sendNotification(String receiver, final String username, final String message)
    {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query =  tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(),R.drawable.mydp,username + ": " + message,
                            "New message", userid);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    try {
                                        Log.e(TAG,response.message());

                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    if(response.code() == 200)
                                    {
                                        if(response.body().success != 1)
                                        {
                                           // Toast.makeText(MessageActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readmessage(final String myid, final String userid, final String imageurl)
    {
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                flag = true;
                if(ConnectionStateMonitor.connected==true)
                {
                mChat.clear();
               // database.clearChatTable();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat chat = snapshot.getValue(Chat.class);
                    //database.addChats(chat);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                        chat.getSender().equals(myid) && chat.getReceiver().equals(userid))
                    {
                        mChat.add(chat);
                    }

                }
                    messageAdapter = new MessageAdapter(getApplicationContext(),mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MessageActivity",databaseError.getMessage());
            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void callfromdatabase()
    {
        readmessage(fuser.getUid(),userid,"default");
        flag = false;
        mChat.clear();
        Cursor cursor = database.viewChatUser();
        if(cursor.getCount()!=0) {
            while ((cursor.moveToNext())) {

                boolean isseen = true;
                if(cursor.getString(4).equals("false"))
                {
                    isseen = false;
                }


                Chat chat = new Chat(cursor.getString(1),cursor.getString(2),cursor.getString(3),isseen);
                if(chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid) ||
                        chat.getSender().equals(fuser.getUid()) && chat.getReceiver().equals(userid))
                {
                    mChat.add(chat);
                }
            }
            messageAdapter = new MessageAdapter(getApplicationContext(),mChat,"default");
            recyclerView.setAdapter(messageAdapter);

        }
    }

    private void status(String status)
    {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenlistener);
        status("offline");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu_bar,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.call_button:
                openCallingActivity();
                break;
            case R.id.video_call:
                openVideoCallingActivity();
                break;
        }
        return true;
    }

    private void openCallingActivity()
    {

        Intent jumptocall= new Intent(MessageActivity.this,CallingActivity.class);

        jumptocall.putExtra("Userid", userid);
        startActivity(jumptocall);
    }

    private void openVideoCallingActivity()
    {
        call = getSinchServiceInterface().callUserVideo(userid);
        String callId = call.getCallId();
        Intent jumptocall= new Intent(MessageActivity.this,CallingActivityVideo.class);
        jumptocall.putExtra(SinchService.CALL_ID, callId);
        jumptocall.putExtra("UserId", userid);
        startActivity(jumptocall);
    }

//    public class Videocall implements VideoCallListener{
//
//        @Override
//        public void onVideoTrackAdded(com.sinch.android.rtc.calling.Call call) {
//            VideoController vc = sinchClient.getVideoController();
//            View myPreview = vc.getLocalView();
//            View remoteView = vc.getRemoteView();
//        }
//
//        @Override
//        public void onVideoTrackPaused(com.sinch.android.rtc.calling.Call call) {
//
//        }
//
//        @Override
//        public void onVideoTrackResumed(com.sinch.android.rtc.calling.Call call) {
//
//        }
//
//        @Override
//        public void onCallProgressing(com.sinch.android.rtc.calling.Call call) {
//
//        }
//
//        @Override
//        public void onCallEstablished(com.sinch.android.rtc.calling.Call call) {
//
//        }
//
//        @Override
//        public void onCallEnded(com.sinch.android.rtc.calling.Call call) {
//
//        }
//
//        @Override
//        public void onShouldSendPushNotification(com.sinch.android.rtc.calling.Call call, List<PushPair> list) {
//
//        }
//    }
}
