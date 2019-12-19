/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.shef.oak.com6510.MyViewModel;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDCallback;
import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.PhotoData;


/**
 * ShowImageByPathView
 * This is for showing all images with the same trip ID.
 * Therefore, it is totally different from ShowImageSortedByPathView, which shows "all images in the database."
 */
public class ShowImageByPathView extends AppCompatActivity implements QueryGetPhotosByTripIDCallback {
    private MyViewModel myViewModel;
    private String tripTitle;
    private int tripId;

    private QueryGetPhotosByTripIDCallback callback;
    private ImageAdapter adapter;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_bypath);
        Intent intent = this.getIntent();
        tripTitle = intent.getStringExtra("EXTRA_TITLE");
        tripId = intent.getIntExtra("EXTRA_ID", -1);
        final TextView textViewTitle = findViewById(R.id.image_path_title);
        textViewTitle.setText(tripTitle);

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // Set up the RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.grid_bypath_recycler);

        // Set an path adapter to the recycler view
        adapter = new ImageAdapter(ShowImageByPathView.this);
        mRecyclerView.setAdapter(adapter);
        int numberOfColumns = 4;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mRecyclerView.setHasFixedSize(true);

        // Retrieve photos based on tripId & Show them in a grid
        callback = ShowImageByPathView.this;
        activity = this;
        myViewModel.getPhotosByTripId(tripId, callback);
    }

    @Override
    public void onRetrieveFinished(List<PhotoData> data) {
        Log.i("debug", "onRetrieveFinished()");
        adapter.setPhotos(data, activity);
        adapter.notifyDataSetChanged();
    }
}
