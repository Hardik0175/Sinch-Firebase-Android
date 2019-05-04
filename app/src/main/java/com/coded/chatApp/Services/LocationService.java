package com.coded.chatApp.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.coded.chatApp.Models.ConnectionStateMonitor;
import com.coded.chatApp.Models.UserLoc;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LocationService extends Service {

    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String TAG = "LocationService";

    public MyLocationListener locationListener;
    public LocationManager locationManager;

    public static LatLng latLng;

    FirebaseAuth auth;
    FirebaseUser user;

    public LocationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        locationListener= new MyLocationListener();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        getLocationPermission();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private class MyLocationListener implements android.location.LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            getDeviceLocation();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void getDeviceLocation(){
        try
        {
            if(ConnectionStateMonitor.connected==true) {
                if (mLocationPermissionGranted == true) {
                    Task locationResults = mFusedLocationProviderClient.getLastLocation();
                    locationResults.addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                mLastKnownLocation = task.getResult();
                                uploadMyloacation(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()));
                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                            }
                        }
                    });
                }
            }
        }
        catch (SecurityException e)
        {
            Log.e("Error Location: ",e.getMessage());
        }
    }

    private void uploadMyloacation(LatLng latLng)
    {

       // UserLoc userLoc = new UserLoc()
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, locationListener);
            getDeviceLocation();
        }
    }
}
