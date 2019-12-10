/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;


import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import oak.shef.ac.uk.livedata.database.PhotoData;
import oak.shef.ac.uk.livedata.database.TripData;

public class MyViewModel extends AndroidViewModel {
    private final MyRepository mRepository;
    private LiveData<List<TripData>> allTrips;
    private LiveData<List<PhotoData>> allPhotos;

    // LiveData<NumberData> numberDataToDisplay;

    public MyViewModel (Application application) {
        super(application);
        // creation and connection to the Repository
        mRepository = new MyRepository(application);
        allTrips = mRepository.getAllTrips();
        allPhotos = mRepository.getAllPhotos();
    }

    public void insertPhoto(PhotoData photo) {
        mRepository.insertPhoto(photo);
    }

    public void insertTrip(TripData trip) {
        mRepository.insertTrip(trip);
    }

    public LiveData<List<TripData>> getAllTrips() {
        return allTrips;
    }

    public LiveData<List<PhotoData>> getAllPhotos() {
        return allPhotos;
    }




    /**
     * getter for the live data
     * @return
     */
    /*
    LiveData<NumberData> getNumberDataToDisplay() {
        if (numberDataToDisplay == null) {
            numberDataToDisplay = new MutableLiveData<NumberData>();
        }
        return numberDataToDisplay;
    }
    */

    /**
     * request by the UI to generate a new random number
     */
    /*
    public void generateNewNumber() {
        mRepository.generateNewNumber();
    }

     */



    /**
     * registerPhoto -> to be replaced by insertPhoto
     * Desc: make a new PhotoData entry and register it to the database.
     * @param uri this is String type. Be careful.
     * @param timeStamp String.
     */
    public void registerPhoto(String uri, String timeStamp, Float pressureValue, Float temperatureValue) {
        mRepository.insertPhoto(new PhotoData(uri, timeStamp, pressureValue, temperatureValue));
    }
}
