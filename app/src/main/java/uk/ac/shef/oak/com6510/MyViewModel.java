/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;


import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import uk.ac.shef.oak.com6510.database.PhotoData;
import uk.ac.shef.oak.com6510.database.TripData;

public class MyViewModel extends AndroidViewModel {
    private final MyRepository mRepository;
    private LiveData<List<TripData>> allTrips;
    private LiveData<List<PhotoData>> allPhotos;
    LiveData<TripData> newTrip;

    public MyViewModel (Application application) {
        super(application);
        // creation and connection to the Repository
        mRepository = new MyRepository(application);
        allTrips = mRepository.getAllTrips();
        allPhotos = mRepository.getAllPhotos();
    }

    /**
     * insertPhoto
     * Desc: make a new PhotoData entry and register it to the database by passing data to the repository.
     * @param uri String
     * @param timeStamp
     * @param pressureValue
     * @param temperatureValue
     */
    public void insertPhoto(String uri, String timeStamp, Float pressureValue, Float temperatureValue) {
        mRepository.insertPhoto(new PhotoData(uri, timeStamp, pressureValue, temperatureValue));
    }

    /**
     * insertTrip
     * Desc: make a new TripData entry and register it to the database.
     * @param tripTitle
     * @param timeStamp
     */
    public void insertTrip(String tripTitle, String timeStamp) {
        mRepository.insertTrip(new TripData(tripTitle, timeStamp));
    }

    public LiveData<List<TripData>> getAllTrips() {
        return allTrips;
    }

    public LiveData<List<PhotoData>> getAllPhotos() {
        return allPhotos;
    }
}
