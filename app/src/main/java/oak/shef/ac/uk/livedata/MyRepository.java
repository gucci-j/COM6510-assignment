/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import oak.shef.ac.uk.livedata.database.MyRoomDatabase;
import oak.shef.ac.uk.livedata.database.PhotoDAO;
import oak.shef.ac.uk.livedata.database.PhotoData;
import oak.shef.ac.uk.livedata.database.TripDAO;
import oak.shef.ac.uk.livedata.database.TripData;
import pl.aprilapps.easyphotopicker.EasyImage;

class MyRepository extends ViewModel {
    private final TripDAO mTripDBDao;
    private final PhotoDAO mPhotoDBDao;
    // for storing all trips
    private LiveData<List<TripData>> allTrips;
    // for storing all photos
    private LiveData<List<PhotoData>> allPhotos;

    // for storing images -> to be removed
    private List<ImageElement> myPictureList = new ArrayList<>();


    public MyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        mTripDBDao = db.tripDao();
        mPhotoDBDao = db.photoDao();
        allTrips = mTripDBDao.getAllTrips();
        allPhotos = mPhotoDBDao.getAllPhotos();
    }


    /**
     * insertPhoto
     * Desc: insert a new photo with this method.
     *       a photo can be added by using a asynchronous task,
     *       which is implemented by insertPhotoAsyncTask.
     * @param photo PhotoData from ViewModel (View)
     */
    public void insertPhoto(PhotoData photo) {
        new insertPhotoAsyncTask(mPhotoDBDao).execute(photo);
    }

    private static class insertPhotoAsyncTask extends AsyncTask<PhotoData, Void, Void> {
        // We need this because the class is static and cannot access to the repository.
        private PhotoDAO mPhotoAsyncTaskDao;

        private insertPhotoAsyncTask(PhotoDAO mPhotoAsyncTaskDao) {
            this.mPhotoAsyncTaskDao = mPhotoAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(PhotoData... photos) {
            // this can be extended with multiple images.
            Log.i("MyRepository", "Photo registered: " +photos[0].getFilename()+ " " +
                    photos[0].getTime());
            mPhotoAsyncTaskDao.insert(photos[0]);
            return null;
        }
    }

    /**
     * insertTrip
     * Desc: insert a new trip to the database.
     *       a trip can be added by using a asynchronous task.
     * @param trip TripData from ViewModel (View)
     */
    public void insertTrip(TripData trip) {
        new insertTripAsyncTask(mTripDBDao).execute(trip);
    }

    private static class insertTripAsyncTask extends AsyncTask<TripData, Void, Void> {
        // We need this because the class is static and cannot access to the repository.
        private TripDAO mTripAsyncTaskDao;

        private insertTripAsyncTask(TripDAO mTripAsyncTaskDao) {
            this.mTripAsyncTaskDao = mTripAsyncTaskDao;
        }

        @Override
        protected Void doInBackground(TripData... trips) {
            Log.i("MyRepository", "Trip registered: " +trips[0].getId()+ " " +
                    trips[0].getTitle()+ " " +trips[0].getDate());
            mTripAsyncTaskDao.insert(trips[0]);
            return null;
        }
    }

    // Add delete and update if possible here!
    public LiveData<List<TripData>> getAllTrips() {
        return allTrips;
    }
    public LiveData<List<PhotoData>> getAllPhotos() {
        return allPhotos;
    }


    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    /*
    public LiveData<NumberData> getNumberData() {
        return mDBDao.retrieveOneNumber();
    }
     */

    /**
     * called by the UI to request the generation of a new random number
     */
    /*
    public void generateNewNumber() {
        Random r = new Random();
        int i1 = r.nextInt(10000 - 1) + 1;
        new insertAsyncTask(mDBDao).execute(new NumberData(i1));
    }
     */

    /*
    private static class insertAsyncTask extends AsyncTask<NumberData, Void, Void> {
        private MyDAO mAsyncTaskDao;
        private LiveData<NumberData> numberData;

        insertAsyncTask(MyDAO dao) {
            mAsyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final NumberData... params) {
            mAsyncTaskDao.insert(params[0]);
            Log.i("MyRepository", "number generated: "+params[0].getNumber()+"");
            // you may want to uncomment this to check if numbers have been inserted
            //            int ix=mAsyncTaskDao.howManyElements();
            //            Log.i("TAG", ix+"");
            return null;
        }
    }
     */


    public void savePhoto(List<File> returnedPhotos) {
        myPictureList.addAll(getImageElements(returnedPhotos));
        Log.i("debug", "savePhoto: Image has been added");
    }

    /**
     * given a list of photos, it creates a list of myElements
     * @param returnedPhotos
     * @return
     */
    private List<ImageElement> getImageElements(List<File> returnedPhotos) {
        List<ImageElement> imageElementList = new ArrayList<>();
        for (File file: returnedPhotos){
            ImageElement element= new ImageElement(file);
            imageElementList.add(element);
        }
        return imageElementList;
    }

    public List<ImageElement> getPhotos() {
        Log.i("debug", "getPhotos: Images have been returned");
        Log.i("debug", String.valueOf(myPictureList.size()));
        return myPictureList;
    }
}
