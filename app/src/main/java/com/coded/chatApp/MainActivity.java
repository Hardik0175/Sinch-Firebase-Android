package com.coded.chatApp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.coded.chatApp.Models.User;
import com.coded.chatApp.SinchCall.BaseActivity;
import com.coded.chatApp.SinchCall.SinchService;
import com.coded.chatApp.fragments.ContactsFragment;
import com.coded.chatApp.fragments.MapFragment;
import com.coded.chatApp.fragments.UserFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements SinchService.StartFailedListener {

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    CircleImageView profilePic;

    Toolbar bar;
    TextView username;

    public static ArrayList<String> arrayList_Android_Contacts;
    public static boolean isloaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        username = findViewById(R.id.status);
        profilePic = findViewById(R.id.mydp);
        bar = findViewById(R.id.mybar);

        setSupportActionBar(bar);
        getSupportActionBar().setTitle("");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User myuser = dataSnapshot.getValue(User.class);
                username.setText(myuser.getUsername());
                if (myuser.getImageurl().equals("default")) {
                    profilePic.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(myuser.getImageurl()).into(profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewpager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragement(new UserFragment(), "Users");
        viewPagerAdapter.addFragement(new ContactsFragment(), "Contacts");
        viewPagerAdapter.addFragement(new MapFragment(),"Maps");
        viewpager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewpager);

        new loadcontacts().execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                auth.signOut();
                Intent startActivity = new Intent(MainActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startActivity);
                return true;

        }
        return false;
    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        /**
         * @see UserFragment
         * @see ContactsFragment
         * @param fragment For scroll view
         * @param title Heading of the layout
         */
        public void addFragement(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    /**
     * @see User
     * @param status Current status User
     */
    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

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
        status("offline");
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        getSinchServiceInterface().startClient(user.getUid());

    }

    public class Android_Contact {
        public String android_contact_TelefonNr = "";
    }


    public class loadcontacts extends AsyncTask<String,Void,ArrayList> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//                dialog = ProgressDialog.show(MainActivity.this, "",
//                        "Loading Contacts. Please wait...", true);
        }

        @Override
        protected ArrayList doInBackground(String... strings) {

            arrayList_Android_Contacts = new ArrayList<String>();
            Cursor cursor_Android_Contacts = null;
            ContentResolver contentResolver = getContentResolver();
            try {
                cursor_Android_Contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            } catch (Exception ex) {
               // Log.e("Error on contact", ex.getMessage());
            }
            if (cursor_Android_Contacts.getCount() > 0) {

                while (cursor_Android_Contacts.moveToNext()) {
                    Android_Contact android_contact = new Android_Contact();
                    String contact_id = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts._ID));
                    int hasPhoneNumber = Integer.parseInt(cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {
                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                                , null
                                , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                                , new String[]{contact_id}
                                , null);

                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            String mynumber = phoneNumber.replace(" ","");

                            arrayList_Android_Contacts.add(mynumber);
                          //  Log.e("Phone number:", mynumber);

                        }
                        phoneCursor.close();
                    }

                }
                return arrayList_Android_Contacts;

            }
            else
            {
                return null;
            }

        }

        @Override
        protected void onPostExecute(ArrayList arrayList) {
            super.onPostExecute(arrayList);
//            dialog.cancel();
            isloaded=true;
        }
    }

}
