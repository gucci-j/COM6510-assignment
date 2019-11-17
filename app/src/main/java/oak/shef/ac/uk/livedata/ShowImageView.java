/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ShowImageView extends AppCompatActivity {
    private MyViewModel myViewModel;
    private Activity activity;
    // private List<ImageElement> myPictureList = new ArrayList<>(); // for storing images

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        Intent intent = this.getIntent();
        activity = this;
        // Get a new or existing ViewModel from the ViewModelProvider.
        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);
        // Add an observer on the LiveData. The onChanged() method fires
        // when the observed data changes and the activity is
        // in the foreground.


    }
}
