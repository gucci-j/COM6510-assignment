/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

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

/**
 * ShowImageView
 * This is for showing all images by asc order.
 */
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

        // Select browsing views
        // REf: https://developer.android.com/guide/topics/ui/controls/spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                // String item = (String) adapterView.getSelectedItem();
                // Toast.makeText(ShowImageView.this, item+" "+pos, Toast.LENGTH_SHORT).show();
                switch (pos) {
                    case 0:
                        // do nothing
                        break;

                    case 1:
                        // move to ShowImageSortedByPathView
                        Intent intent_case1 = new Intent(ShowImageView.this, ShowImageSortedByPathView.class);
                        startActivity(intent_case1);
                        break;

                    case 2:
                        // move to ShowPathView
                        Intent intent_case2 = new Intent(ShowImageView.this, ShowPathView.class);
                        startActivity(intent_case2);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Another interface callback
            }
        });
    }
}
