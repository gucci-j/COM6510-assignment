/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShowImageView extends AppCompatActivity {
    private MyViewModel myViewModel;
    private List<ImageElement> myPictureList;
    private RecyclerView.Adapter  mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Intent intent = this.getIntent();
        myPictureList = (ArrayList<ImageElement>)getIntent().getSerializableExtra("IMG");

        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        // Add an observer on the LiveData. The onChanged() method fires
        // when the observed data changes and the activity is
        // in the foreground.

        // set up the RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.grid_recycler_view);
        int numberOfColumns = 4;
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mAdapter= new ImageAdapter(ShowImageView.this, myPictureList);
        mRecyclerView.setAdapter(mAdapter);
        // mAdapter.setItems(initData());
        // mAdapter.setItems(myPictureList);
        // mAdapter.notifyDataSetChanged();
    }

    private List<ImageElement> initData() {
        List<ImageElement> PictureList = new ArrayList<>();
        PictureList.add(new ImageElement(R.drawable.joe1));
        PictureList.add(new ImageElement(R.drawable.joe2));
        PictureList.add(new ImageElement(R.drawable.joe3));
        PictureList.add(new ImageElement(R.drawable.joe1));
        PictureList.add(new ImageElement(R.drawable.joe2));
        PictureList.add(new ImageElement(R.drawable.joe3));
        PictureList.add(new ImageElement(R.drawable.joe1));
        return PictureList;
    }
}
