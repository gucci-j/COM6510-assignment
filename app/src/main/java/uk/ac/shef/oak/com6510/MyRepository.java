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

import uk.ac.shef.oak.com6510.Browse.ImageAdapter;
import uk.ac.shef.oak.com6510.database.MyRoomDatabase;
import uk.ac.shef.oak.com6510.database.PhotoDAO;
import uk.ac.shef.oak.com6510.database.PhotoData;
import uk.ac.shef.oak.com6510.database.TripDAO;
import uk.ac.shef.oak.com6510.database.TripData;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetFullPathByTripIdCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetPhotosByTripIDWAdapterCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryGetTitleByTripIdCallback;
import uk.ac.shef.oak.com6510.database.callbacks.QueryInsertTripCallback;


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

    // LiveData for getting all photos and trips
    public LiveData<List<TripData>> getAllTrips() {
        return allTrips;
    }
    public LiveData<List<PhotoData>> getAllPhotos() {
        return allPhotos;
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
                    photos[0].getTime() +" "+ photos[0].getPressureValue() +" "+ photos[0].getTemperatureValue()+" "+photos[0].getGPSLatitude()+" "+photos[0].getGPSLongitude());
            return null;
        }
    }

    /**
     * updateTrip
     * Desc: update the full path with this trip id. The
     *       updateAsyncTask function is to modify the database in async way
     *       otherwise, will conflict with main thread
     * @param tripId  id of tripdata which need to be modify
     * @param fullPath  the total path in string type
     */
    public void updateTrip(int tripId, String fullPath) {
        new updateTripAsyncTask(mTripDBDao,fullPath).execute(tripId);
    }

    private static class updateTripAsyncTask extends AsyncTask<Integer, Void, Void> {
        // We need this because the class is static and cannot access to the repository.
        private TripDAO mTripAsyncTaskDao;
        private String fullPath;

        private updateTripAsyncTask(TripDAO mTripAsyncTaskDao, String fullPath) {
            this.mTripAsyncTaskDao = mTripAsyncTaskDao;
            this.fullPath = fullPath;
        }

        @Override
        protected Void doInBackground(Integer... tripId) {
            mTripAsyncTaskDao.update(tripId[0], fullPath);
            return null;
        }
    }

    /**
     * insertTrip
     * Desc: insert a new trip to the database.
     *       a trip can be added by using a asynchronous task.
     * @param trip TripData from ViewModel (View)
     */
    public void insertTrip(TripData trip, QueryInsertTripCallback callback) {
        new insertTripAsyncTask(mTripDBDao, callback).execute(trip);
    }

    // AsyncTask<Params, Progress, Result>
    private static class insertTripAsyncTask extends AsyncTask<TripData, Void, Long> {
        // We need this because the class is static and cannot access to the repository.
        private TripDAO mTripAsyncTaskDao;
        private QueryInsertTripCallback callback;

        private insertTripAsyncTask(TripDAO mTripAsyncTaskDao, QueryInsertTripCallback callback) {
            this.mTripAsyncTaskDao = mTripAsyncTaskDao;
            this.callback = callback;
        }

        @Override
        protected Long doInBackground(TripData... trips) {
            long trip_id = mTripAsyncTaskDao.insert(trips[0]);
            Log.i("debug/MyRepository", "insertTripAsyncTask (trip registered): "+
                    trip_id + " " + trips[0].getTitle()+ " " +trips[0].getDate());
            return trip_id;
        }


        @Override
        protected void onPostExecute(Long result) {
            // notify to the UI
            callback.onInsertFinished(result.intValue());
        }
    }

    /**
     * getTripTitle
     * Desc: get the title with the particular id
     *       asynctask is also called
     *       callback is an interface to retrieve data, which implemented in the target activity
     * @param tripId  id of tripdata which need to be modify
     * @param callback the interface
     */
    public void getTripTitle(int tripId, QueryGetTitleByTripIdCallback callback) {
        getTripTitleAsyncTask task= new getTripTitleAsyncTask(mTripDBDao,callback);
        task.execute(tripId);
    }
    private static class getTripTitleAsyncTask extends AsyncTask<Integer, Void, String>{
        private TripDAO mTripAsyncTaskDao;
        private String tripTitle;
        private QueryGetTitleByTripIdCallback callback;

        private getTripTitleAsyncTask(TripDAO mTripAsyncTaskDao,QueryGetTitleByTripIdCallback callback){
            this.mTripAsyncTaskDao=mTripAsyncTaskDao;
            this.callback=callback;

        }
        @Override
        protected String doInBackground(Integer...tripId){
            tripTitle=mTripAsyncTaskDao.getTripTitle(tripId[0]);
            return mTripAsyncTaskDao.getTripTitle(tripId[0]);
        }
        @Override
        protected void onPostExecute(String title) {
            super.onPostExecute(title);
            tripTitle = title;
            callback.onRetrieveFinished(title);
        }
    }

    /**
     * getFullPath
     * Desc: get the full path using async way
     * @param tripId  id of tripdata which need to be modify
     * @param callback the interface
     */
    public void getFullPath(int tripId, QueryGetFullPathByTripIdCallback callback) {
        getTripFullPathAsyncTask task= new getTripFullPathAsyncTask(mTripDBDao,callback);
        task.execute(tripId);
    }
    private static class getTripFullPathAsyncTask extends AsyncTask<Integer, Void, String>{
        private TripDAO mTripAsyncTaskDao;
        private QueryGetFullPathByTripIdCallback callback;

        private getTripFullPathAsyncTask(TripDAO mTripAsyncTaskDao,QueryGetFullPathByTripIdCallback callback){
            this.mTripAsyncTaskDao=mTripAsyncTaskDao;
            this.callback=callback;

        }
        @Override
        protected String doInBackground(Integer...tripId){
            return mTripAsyncTaskDao.getFullPath(tripId[0]);
        }
        @Override
        protected void onPostExecute(String fullPath) {
            super.onPostExecute(fullPath);
            System.out.println("THE FULLPATH3"+fullPath);
            callback.onRetrieveFullPathFinished(fullPath);

        }

    }

    /**
     * getPhotosByTripId
     * Desc: get photo data with an id using async way
     * @param id  id of tripdata which need to be modify
     * @param callback the interface
     */
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

    /**
     * getPhotosByTripId
     * Desc: get photo data by trip id with adapter using async way
     * @param id  id of tripdata which need to be modify
     * @param callback  the interface
     * @param adapter  the return adapter for storing image
     */
    public void getPhotosByTripIdWAdapter(int id, QueryGetPhotosByTripIDWAdapterCallback callback, ImageAdapter adapter) {
        new getPhotosByTripIdWAdapterAsyncTask(mPhotoDBDao, callback, adapter).execute(id);
    }

    private static class getPhotosByTripIdWAdapterAsyncTask extends AsyncTask<Integer, Void, List<PhotoData>> {
        // We need this because the class is static and cannot access to the repository.
        private PhotoDAO mPhotoAsyncTaskDao;
        private QueryGetPhotosByTripIDWAdapterCallback callback;
        private ImageAdapter adapter;

        private getPhotosByTripIdWAdapterAsyncTask(PhotoDAO mPhotoAsyncTaskDao,
                                           QueryGetPhotosByTripIDWAdapterCallback callback,
                                           ImageAdapter adapter) {
            this.mPhotoAsyncTaskDao = mPhotoAsyncTaskDao;
            this.callback = callback;
            this.adapter = adapter;
        }

        @Override
        protected List<PhotoData> doInBackground(Integer... tripId) {
            List<PhotoData> data = mPhotoAsyncTaskDao.getTripPhotosASync(tripId[0]);
            return data;
        }

        @Override
        protected void onPostExecute(List<PhotoData> data) {
            // notify to the UI
            callback.onRetrieveFinished(data, adapter);
        }
    }
}
