/*
 * Copyright (c) 2018. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

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


}
