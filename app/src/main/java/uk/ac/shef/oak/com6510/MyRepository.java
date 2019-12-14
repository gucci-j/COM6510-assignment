/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import uk.ac.shef.oak.com6510.database.MyRoomDatabase;
import uk.ac.shef.oak.com6510.database.PhotoDAO;
import uk.ac.shef.oak.com6510.database.PhotoData;
import uk.ac.shef.oak.com6510.database.TripDAO;
import uk.ac.shef.oak.com6510.database.TripData;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDCallback;


class MyRepository extends ViewModel {
    private final TripDAO mTripDBDao;
    private final PhotoDAO mPhotoDBDao;
    private MyRoomDatabase db;

    // for storing all trips
    private LiveData<List<TripData>> allTrips;
    // for storing all photos
    private LiveData<List<PhotoData>> allPhotos;


    public MyRepository(Application application) {
        db = MyRoomDatabase.getDatabase(application);
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
        Log.d("debug/MyRepository","insertPhoto: Got data");
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
            mPhotoAsyncTaskDao.insert(photos[0]);
            Log.i("debug/MyRepository", "insertPhotoAsyncTask (photo registered): " +photos[0].getFilename()+ " " +
                    photos[0].getTime() +" "+ photos[0].getPressureValue() +" "+ photos[0].getTemperatureValue()+" "+photos[0].getGPSValue());
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

    // AsyncTask<Params, Progress, Result>
    private static class insertTripAsyncTask extends AsyncTask<TripData, Void, Long> {
        // We need this because the class is static and cannot access to the repository.
        private TripDAO mTripAsyncTaskDao;

        private insertTripAsyncTask(TripDAO mTripAsyncTaskDao) {
            this.mTripAsyncTaskDao = mTripAsyncTaskDao;
        }

        @Override
        protected Long doInBackground(TripData... trips) {
            long trip_id = mTripAsyncTaskDao.insert(trips[0]);
            Log.i("debug/MyRepository", "insertTripAsyncTask (trip registered): "+
                    trip_id + " " + trips[0].getTitle()+ " " +trips[0].getDate());
            return trip_id;
        }

        /*
        @Override
        protected void onPostExecute(Long result) {
            aRes.processFinish((int)(long) result);
        }
         */
    }

    // Add delete and update if possible here!
    public LiveData<List<TripData>> getAllTrips() {
        return allTrips;
    }
    public LiveData<List<PhotoData>> getAllPhotos() {
        return allPhotos;
    }
    public TripData getTrip(String title, String date) {
        return mTripDBDao.getTrip(title, date);
    }


    public void getPhotosByTripId(int id, QueryGetPhotosByTripIDCallback callback) {
        new getPhotosByTripIdAsyncTask(mPhotoDBDao, callback).execute(id);
    }

    private static class getPhotosByTripIdAsyncTask extends AsyncTask<Integer, Void, List<PhotoData>> {
        // We need this because the class is static and cannot access to the repository.
        private PhotoDAO mPhotoAsyncTaskDao;
        private QueryGetPhotosByTripIDCallback callback;

        private getPhotosByTripIdAsyncTask(PhotoDAO mPhotoAsyncTaskDao, QueryGetPhotosByTripIDCallback callback) {
            this.mPhotoAsyncTaskDao = mPhotoAsyncTaskDao;
            this.callback = callback;
        }

        @Override
        protected List<PhotoData> doInBackground(Integer... tripId) {
            List<PhotoData> data = mPhotoAsyncTaskDao.getTripPhotosASync(tripId[0]);
            return data;
        }

        @Override
        protected void onPostExecute(List<PhotoData> data) {
            // notify to the UI
            callback.onRetrieveFinished(data);
        }
    }
}