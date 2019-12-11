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

public class StartTrackingView extends AppCompatActivity {
    private MyViewModel myViewModel;
    private String title;
    private String timeStamp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracking);

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // Here we can proceed to the next activity.
        // Some parts of the code should be in viewmodel or repo.
        myViewModel.getAllTrips().observe(StartTrackingView.this, new Observer<List<TripData>>() {
            @Override
            public void onChanged(@Nullable List<TripData> tripData) {
                int trip_id;
                for (TripData trip: tripData) {
                    if (trip.getTitle().equals(title) && trip.getDate().equals(timeStamp)) {
                        trip_id = trip.getId();
                        Log.i("debug", "id: "+ trip.getId()+ " title: "+trip.getTitle()+ " date: "+trip.getDate());
                        Intent intent = new Intent(StartTrackingView.this, Maps.class);
                        intent.putExtra("TRIP_ID", trip_id);
                        startActivity(intent);
                    } else {
                        Log.i("debug", "(MISMATCHED) id: "+ trip.getId()+ " title: "+trip.getTitle()+" date: "+trip.getDate());
                    }
                }
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
}
