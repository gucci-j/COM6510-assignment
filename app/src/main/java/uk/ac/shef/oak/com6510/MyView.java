/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

import uk.ac.shef.oak.com6510.database.PhotoData;
import uk.ac.shef.oak.com6510.database.TripData;

public class MyView extends AppCompatActivity {
    private MyViewModel myViewModel;

    // for taking & uploading a photo
    private final static int RESULT_CAMERA = 1001;
    private final static int REQUEST_PERMISSION = 1002;
    private static final int READ_REQUEST_CODE = 42;
    private Uri cameraURI;
    private File cameraFILE;
    private String timeStamp;
    private boolean type;

    //for barometer and thermometer
    private Barometer barometer;
    private Thermometer thermometer;
    private Float currentPressureValue;
    private Float currentTemperatureValue;

    //for location
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        // Add an observer on the LiveData. The onChanged() method fires
        // when the observed data changes and the activity is
        // in the foreground.
        myViewModel.getAllPhotos().observe(this, new Observer<List<PhotoData>>() {
            @Override
            public void onChanged(@Nullable List<PhotoData> photoData) {
                // Update view here
                Toast.makeText(MyView.this, "onChanged(Photos)",
                        Toast.LENGTH_SHORT).show();
            }
        });
        myViewModel.getAllTrips().observe(this, new Observer<List<TripData>>() {
            @Override
            public void onChanged(@Nullable List<TripData> tripData) {
                // Update view here
                Toast.makeText(MyView.this, "onChanged(Trips)",
                        Toast.LENGTH_SHORT).show();
            }
        });


        // Click 'start' then turn to the Maps view
        Button trackingStart=findViewById(R.id.tracking);
        trackingStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Intent intent=new Intent(MyView.this,Maps.class);
                Intent intent = new Intent(MyView.this, StartTrackingView.class);
                startActivity(intent);
            }
        });



        // for browsing
        // Ref: https://ideacloud.co.jp/dev/android_studio_intent.html
        Button buttonBrowse = findViewById(R.id.browsing);
        buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyView.this, ShowImageView.class);
                startActivity(intent);
            }
        });


    }





}

