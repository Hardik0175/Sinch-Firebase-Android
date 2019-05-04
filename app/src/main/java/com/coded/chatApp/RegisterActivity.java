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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    Toolbar bar;

    MaterialEditText username, emailid, password, phone, extension;

    Button register;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bar = findViewById(R.id.mybar);
        username = findViewById(R.id.status);
        emailid = findViewById(R.id.emailID);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        phone = findViewById(R.id.phone_number);
        extension = findViewById(R.id.extension);

        auth = FirebaseAuth.getInstance();

        setSupportActionBar(bar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username_t = username.getText().toString();
                String emailid_t = emailid.getText().toString();
                String password_t = password.getText().toString();
                String phonenumber = phone.getText().toString();
                String ext = extension.getText().toString();

                if (!TextUtils.isEmpty(username_t) || !TextUtils.isEmpty(emailid_t) || !TextUtils.isEmpty(password_t)) {
                    if (password_t.length() < 6) {
                        Toast.makeText(getApplicationContext(), "Password should be longer then 6 characters", Toast.LENGTH_LONG).show();
                    } else {
                        if(phonenumber.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "Phone Number is Required", Toast.LENGTH_LONG).show();
                        }
                        else if (!phonenumber.isEmpty() || ext.isEmpty() ) {
                            register(username_t, emailid_t, password_t, "+91"+phonenumber);
                        }
                        else if(!phonenumber.isEmpty() || !ext.isEmpty())
                        {
                            register(username_t, emailid_t, password_t, "+"+ext+phonenumber);
                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "All fields are compulsory", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void register(final String username, String email, String password, final String phonenumber) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {


                    FirebaseUser myuser = auth.getCurrentUser();

                    String usrid = myuser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(usrid);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", usrid);
                    hashMap.put("username", username);
                    hashMap.put("imageurl", "default");
                    hashMap.put("phonenumber",phonenumber);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Intent intentService = new Intent(getApplicationContext(), LocationService.class);
                                startService(intentService);
                                Intent jumptomain = new Intent(getApplicationContext(), MainActivity.class);
                                jumptomain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(jumptomain);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please enter a valid email id and password", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }
            }
        });

    }


}
