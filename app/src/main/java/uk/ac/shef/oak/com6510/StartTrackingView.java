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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.ac.shef.oak.com6510.database.TripData;
import uk.ac.shef.oak.com6510.database.callbacks.QueryInsertTripCallback;

public class StartTrackingView extends AppCompatActivity implements QueryInsertTripCallback {

    private MyViewModel myViewModel;
    private String title;
    private String timeStamp;

    private QueryInsertTripCallback callback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracking);
        callback = StartTrackingView.this;

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // for storing a trip title
        final EditText tripTitle = findViewById(R.id.trip_title);

        // Click 'start' then turn to the Maps view
        Button trackingStart = findViewById(R.id.start_tracking);
        trackingStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // obtain a title
                title = tripTitle.getText().toString();
                if(title != null && !title.isEmpty() && !title.trim().isEmpty()) {
                    // obtain a timestamp for a trip
                    timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                    // register a new trip
                    myViewModel.insertTrip(title, timeStamp, callback);
                }else {
                    Toast.makeText(StartTrackingView.this, "The title cannot be empty or white space.",
                            Toast.LENGTH_LONG).show();
                    tripTitle.setText(null);
                }
            }
        });
    }

    @Override
    public void onInsertFinished(int id) {
        // Here we can proceed to the next activity.
        if (id != -1) {
            Intent intent = new Intent(StartTrackingView.this, Maps.class);
            intent.putExtra("EXTRA_TRIP_ID", id);
            startActivity(intent);
        }
    }
}
