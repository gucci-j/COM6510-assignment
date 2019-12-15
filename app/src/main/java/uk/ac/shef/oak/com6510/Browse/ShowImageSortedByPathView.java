/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.shef.oak.com6510.MyViewModel;
import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.TripData;

public class ShowImageSortedByPathView extends AppCompatActivity {
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_sortedbypath);
        Intent intent = this.getIntent();

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_parent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Set an path adapter to the recycler view
        final ImagePathAdapter adapter = new ImagePathAdapter(this, myViewModel);
        recyclerView.setAdapter(adapter);

        // Set up Path Lists
        myViewModel.getAllTrips().observe(this, new Observer<List<TripData>>() {
            @Override
            public void onChanged(List<TripData> tripData) {
                Log.i("debug/onCreate", "getAllTrips(): onChanged()");
                adapter.setPaths(tripData);
            }
        });
    }
}
