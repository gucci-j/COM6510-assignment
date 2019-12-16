/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;


import android.app.Application;
import android.location.Location;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import uk.ac.shef.oak.com6510.Browse.ImageAdapter;
import uk.ac.shef.oak.com6510.database.PhotoData;
import uk.ac.shef.oak.com6510.database.TripData;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDWAdapterCallback;

public class MyViewModel extends AndroidViewModel {
    private final MyRepository mRepository;
    private LiveData<List<TripData>> allTrips;
    private LiveData<List<PhotoData>> allPhotos;

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
    public void insertPhoto(String uri, int tripID, String timeStamp, Float pressureValue, Float temperatureValue, double GPSLatitude, double GPSLongitude) {
        mRepository.insertPhoto(new PhotoData(uri, tripID, timeStamp, pressureValue, temperatureValue, GPSLatitude, GPSLongitude));
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
    public TripData getTrip(String title, String id) {return mRepository.getTrip(title, id); }

    public void getPhotosByTripId(int id, QueryGetPhotosByTripIDCallback callback) {
        mRepository.getPhotosByTripId(id, callback);
    }

    public void getPhotosByTripIdWAdapter(int id, QueryGetPhotosByTripIDWAdapterCallback callback, ImageAdapter adapter) {
        mRepository.getPhotosByTripIdWAdapter(id, callback, adapter);
    }
}
