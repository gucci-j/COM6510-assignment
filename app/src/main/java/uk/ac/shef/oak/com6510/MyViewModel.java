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
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetFullPathByTripIdCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDWAdapterCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetTitleByTripIdCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryInsertTripCallback;

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

    public LiveData<List<TripData>> getAllTrips() {
        return allTrips;
    }
    public LiveData<List<PhotoData>> getAllPhotos() {
        return allPhotos;
    }

    /**
     * insertPhoto
     * Desc: make a new PhotoData entry and register it to the database by passing data to the repository.
     * @param uri String
     * @param timeStamp String
     * @param pressureValue Float
     * @param temperatureValue Float
     */
    public void insertPhoto(String uri, int tripID, String timeStamp, Float pressureValue, Float temperatureValue, double GPSLatitude, double GPSLongitude) {
        mRepository.insertPhoto(new PhotoData(uri, tripID, timeStamp, pressureValue, temperatureValue, GPSLatitude, GPSLongitude));
    }

    /**
     * insertTrip
     * Desc: make a new TripData entry and register it to the database.
     *       callback is used for getting a corresponding trip ID.
     * @param tripTitle String
     * @param timeStamp String
     */
    public void insertTrip(String tripTitle, String timeStamp, QueryInsertTripCallback callback) {
        mRepository.insertTrip(new TripData(tripTitle, timeStamp), callback);
    }


    /**
     * getTripTitle
     * Desc: get a trip title based on a trip ID. The result can be obtained using callback.
     * @param tripId int
     * @param callback Activity
     */
    public void getTripTitle(int tripId,QueryGetTitleByTripIdCallback callback){
        mRepository.getTripTitle(tripId,callback);
    }


    /**
     * getPhotosByTripid
     * Desc: get PhotoData with the same trip id. The results can be obtained using callback.
     * @param id int
     * @param callback Activity
     */
    public void getPhotosByTripId(int id, QueryGetPhotosByTripIDCallback callback) {
        mRepository.getPhotosByTripId(id, callback);
    }


    /**
     * getPhotosByTripIdWAdapter
     * Desc: get PhotoData with the same trip id.
     *       Must give an adapter to notify to the adapter that the dataset has been changed because of this retrieval.
     * @param id int
     * @param callback Activity
     * @param adapter ImageAdapter
     */
    public void getPhotosByTripIdWAdapter(int id, QueryGetPhotosByTripIDWAdapterCallback callback, ImageAdapter adapter) {
        mRepository.getPhotosByTripIdWAdapter(id, callback, adapter);
    }


    /**
     * updateTrip
     * Desc: update a TripData entry to insert the full path data of a tracking.
     * @param tripId int
     * @param fullPath String
     */
    public void updateTrip(int tripId,String fullPath){
        mRepository.updateTrip(tripId,fullPath);
    }


    /**
     * getFullPath
     * Desc: get full path data based on a trip ID. The result is notified via callback.
     *       This is used for showing full path in the detail view of an image.
     * @param tripId int
     * @param callback Activity
     */
    public void getFullPath(int tripId, QueryGetFullPathByTripIdCallback callback){
        mRepository.getFullPath(tripId,callback);
    }


    /**
     * deleteTrip
     * Desc: delete a trip entry which is swiped out by the user in the UI.
     * @param tripData TripData
     */
    public void deleteTrip(TripData tripData) {
        mRepository.deleteTrip(tripData);
    }


    /**
     * deletePhoto
     * Desc; delete a photo when it is long clicked.
     * @param photoId int
     */
    public void deletePhoto(int photoId) {
        mRepository.deletePhoto(photoId);
    }
}
