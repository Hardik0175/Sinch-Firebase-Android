package com.coded.chatApp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.coded.chatApp.Services.LocationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;

    Toolbar bar;
    FancyButton login;

    MaterialEditText email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        login = findViewById(R.id.login);
        email =  findViewById(R.id.emailID);
        password = findViewById(R.id.password);
        bar = findViewById(R.id.mybar);

        setSupportActionBar(bar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_t= email.getText().toString();
                String password_t=password.getText().toString();

                if(!TextUtils.isEmpty(email_t) || !TextUtils.isEmpty(password_t))
                {
                    login(email_t,password_t);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"All fields are compulsory",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void login(final String email, String password)
    {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Intent intentService = new Intent(getApplicationContext(),LocationService.class);
                            startService(intentService);
                            Intent jumptomain= new Intent(getApplicationContext(),MainActivity.class);
                            jumptomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(jumptomain);
                            finish();
                        }

                    }
                });
    }

}


