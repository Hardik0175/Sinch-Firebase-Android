package com.coded.chatApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.coded.chatApp.Services.LocationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mehdi.sakout.fancybuttons.FancyButton;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;


    FancyButton loginbn, registerbn;

    @Override
    protected void onStart() {
        super.onStart();

        user = auth.getCurrentUser();
        if (user != null) {
            Intent jumptomain = new Intent(StartActivity.this, MainActivity.class);
            startActivity(jumptomain);
            startService(new Intent(this, LocationService.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        auth = FirebaseAuth.getInstance();
        loginbn = findViewById(R.id.login);
        registerbn = findViewById(R.id.register);
        loginbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumplogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(jumplogin);
            }
        });

        registerbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumpregister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(jumpregister);
            }
        });

    }

}
