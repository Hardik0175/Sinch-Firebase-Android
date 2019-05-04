package com.coded.chatApp.Models;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.coded.chatApp.Database.Database;
import com.coded.chatApp.MessageActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ConnectionStateMonitor extends ConnectivityManager.NetworkCallback {

    final NetworkRequest networkRequest;

    public static boolean connected = false;
    Database database;
    private Context mContext;
    FirebaseUser fuser;

    public ConnectionStateMonitor(Context context) {
        networkRequest = new NetworkRequest.Builder().
                addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
            mContext = context;
            database = new Database(context,null,null,1);
    }

    public void enable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        connected = true;
        sendQueue();
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        connected = false;

        FirebaseDatabase.getInstance().goOffline();
    }

    @Override
    public void onUnavailable() {
        super.onUnavailable();
        connected = false;
       FirebaseDatabase.getInstance().goOffline();
    }

    public void sendQueue() {
       FirebaseDatabase.getInstance().goOnline();
        Cursor cursor = database.viewQueue("Queue");
        if (cursor.getCount() != 0) {
            while ((cursor.moveToNext())) {
                fuser = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", fuser.getUid());
                hashMap.put("receiver", cursor.getString(2));
                hashMap.put("message", cursor.getString(3));
                hashMap.put("isseen", false);

                reference.child("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Send: ","done");
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d("Send: ","failed");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Send: ","failed");
                    }
                });
            }
        }
    }
}
