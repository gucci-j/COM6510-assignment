/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.ac.shef.oak.com6510.database.TripData;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetTripIDCallback;

public class StartTrackingView extends AppCompatActivity implements QueryGetTripIDCallback {

    private MyViewModel myViewModel;
    private String title;
    private String timeStamp;

    private QueryGetTripIDCallback callback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracking);
        callback = StartTrackingView.this;

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // Here we can proceed to the next activity.
        myViewModel.getAllTrips().observe(StartTrackingView.this, new Observer<List<TripData>>() {
            @Override
            public void onChanged(@Nullable List<TripData> tripData) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TripData trip = myViewModel.getTrip(title, timeStamp);
                        if (trip != null) {
                            Log.i("debug/StartTrackingView", "id: "+ trip.getId()+ " title: "+trip.getTitle()+ " date: "+trip.getDate());
                            callback.onRetrieveFinished(trip.getId());
                        }
                    }
                }).start();
            }
        });

        // for storing a trip title
        final EditText tripTitle = findViewById(R.id.trip_title);

        // Click 'start' then turn to the Maps view
        Button trackingStart = findViewById(R.id.start_tracking);
        trackingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // obtain a title
                title = tripTitle.getText().toString();
                // obtain a timestamp for a trip
                timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                // register a new trip
                myViewModel.insertTrip(title, timeStamp);
            }
        });
    }

    @Override
    public void onRetrieveFinished(int id) {
        if (id != -1) {
            Intent intent = new Intent(StartTrackingView.this, Maps.class);
            intent.putExtra("EXTRA_TRIP_ID", id);
            startActivity(intent);
        }
    }
}
