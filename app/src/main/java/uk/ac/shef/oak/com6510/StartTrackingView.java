/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartTrackingView extends AppCompatActivity {
    private MyViewModel myViewModel;
    private EditText tripTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tracking);

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // for storing a trip title
        tripTitle = findViewById(R.id.trip_title);

        // Click 'start' then turn to the Maps view
        Button trackingStart = findViewById(R.id.start_tracking);
        trackingStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // obtain a title
                String title = tripTitle.getText().toString();
                // obtain a timestamp for a trip
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                // register a new trip
                myViewModel.insertTrip(title, timeStamp);

                Intent intent = new Intent(StartTrackingView.this, Maps.class);
                startActivity(intent);
            }
        });
    }
}
