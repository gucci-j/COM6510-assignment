/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.shef.oak.com6510.MyViewModel;
import uk.ac.shef.oak.com6510.R;
import uk.ac.shef.oak.com6510.database.TripData;

public class ShowPathView extends AppCompatActivity {
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_path);
        Intent intent = this.getIntent();

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.browse_path_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // Set an path adapter to the recycler view
        final PathAdapter adapter = new PathAdapter();
        recyclerView.setAdapter(adapter);

        // Set up Path Lists
        myViewModel.getAllTrips().observe(this, new Observer<List<TripData>>() {
            @Override
            public void onChanged(List<TripData> tripData) {
                adapter.setPaths(tripData);
            }
        });

        adapter.setOnItemClickListener(new PathAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TripData data) {
                Intent intent = new Intent(ShowPathView.this, ShowImageByPathView.class);
                intent.putExtra("EXTRA_ID", data.getId());
                intent.putExtra("EXTRA_TITLE", data.getTitle());
                startActivity(intent);
            }
        });

        // Select browsing views
        // REf: https://developer.android.com/guide/topics/ui/controls/spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setSelection(2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                // String item = (String) adapterView.getSelectedItem();
                // Toast.makeText(ShowImageView.this, item+" "+pos, Toast.LENGTH_SHORT).show();
                switch (pos) {
                    case 0:
                        // move to ShowImageView
                        Intent intent_case0 = new Intent(ShowPathView.this, ShowImageView.class);
                        startActivity(intent_case0);
                        break;

                    case 1:
                        // move to ShowImageSortedByPathView
                        Intent intent_case1 = new Intent(ShowPathView.this, ShowImageSortedByPathView.class);
                        startActivity(intent_case1);
                        break;

                    case 2:
                        // do nothing
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
