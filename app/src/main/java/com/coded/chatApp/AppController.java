package com.coded.chatApp;

import android.app.Application;

import com.coded.chatApp.Models.ConnectionStateMonitor;
import com.google.firebase.FirebaseApp;

public class AppController extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());

        ConnectionStateMonitor connectionStateMonitor = new ConnectionStateMonitor(getApplicationContext());
        connectionStateMonitor.enable(this);
    }
}
