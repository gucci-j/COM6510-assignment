/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.shef.oak.com6510.MyViewModel;
import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.PhotoData;

public class ShowImageView extends AppCompatActivity {
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Intent intent = this.getIntent();

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // Set up the RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.grid_recycler);
        int numberOfColumns = 4;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        // Set an image adapter to the recyclerview
        final ImageAdapter mAdapter = new ImageAdapter(ShowImageView.this);
        mRecyclerView.setAdapter(mAdapter);

        // Set up Picture Lists
        myViewModel.getAllPhotos().observe(this, new Observer<List<PhotoData>>() {
            @Override
            public void onChanged(@Nullable List<PhotoData> photoData) {
                // Update view here
                mAdapter.setPhotos(photoData, ShowImageView.this);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
