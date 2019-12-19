/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.shef.oak.com6510.MyViewModel;
import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.TripData;


/**
 * ShowIMageSortedByPathView
 * This is for showing all images in the database sorted by path.
 */
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

        // Swipe to delete path data
        // NOTE: because we set onDestory = CASCADE in PhotoData (Entity), the PhotoData entry will also be deleted.
        // (not the original data in the gallery)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                myViewModel.deleteTrip(adapter.getTripBySwipe(viewHolder.getAdapterPosition()));
                Toast.makeText(ShowImageSortedByPathView.this, "Path data has been deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        // Select browsing views
        // REf: https://developer.android.com/guide/topics/ui/controls/spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                // String item = (String) adapterView.getSelectedItem();
                // Toast.makeText(ShowImageView.this, item+" "+pos, Toast.LENGTH_SHORT).show();
                switch (pos) {
                    case 0:
                        // move to ShowImageView
                        Intent intent_case0 = new Intent(ShowImageSortedByPathView.this, ShowImageView.class);
                        startActivity(intent_case0);
                        break;

                    case 1:
                        // do nothing
                        break;

                    case 2:
                        // move to ShowPathView
                        Intent intent_case2 = new Intent(ShowImageSortedByPathView.this, ShowPathView.class);
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
