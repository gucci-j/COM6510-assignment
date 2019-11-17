/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.List;

import oak.shef.ac.uk.livedata.database.NumberData;

public class MyViewModel extends AndroidViewModel {
    private final MyRepository mRepository;

    LiveData<NumberData> numberDataToDisplay;

    public MyViewModel (Application application) {
        super(application);
        // creation and connection to the Repository
        mRepository = new MyRepository(application);
        numberDataToDisplay = mRepository.getNumberData();
    }


    /**
     * getter for the live data
     * @return
     */
    LiveData<NumberData> getNumberDataToDisplay() {
        if (numberDataToDisplay == null) {
            numberDataToDisplay = new MutableLiveData<NumberData>();
        }
        return numberDataToDisplay;
    }

    /**
     * request by the UI to generate a new random number
     */
    public void generateNewNumber() {
        mRepository.generateNewNumber();
    }


    /**
     * Change UI to show a camera button
     * @param button
     */
    public void setCameraButton(FloatingActionButton button) {
        Log.i("Debug", "setCameraButton: Make it visible");
        if (button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Save photos
     * @param returnedPhotos
     */
    public void saveImage(List<File> returnedPhotos) {
        mRepository.savePhoto(returnedPhotos);
    }
}
