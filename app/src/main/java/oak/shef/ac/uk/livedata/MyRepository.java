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

import oak.shef.ac.uk.livedata.database.MyDAO;
import oak.shef.ac.uk.livedata.database.MyRoomDatabase;
import oak.shef.ac.uk.livedata.database.NumberData;
import pl.aprilapps.easyphotopicker.EasyImage;

class MyRepository extends ViewModel {
    private final MyDAO mDBDao;
    // for storing images
    private List<ImageElement> myPictureList = new ArrayList<>();

    public MyRepository(Application application) {
        MyRoomDatabase db = MyRoomDatabase.getDatabase(application);
        mDBDao = db.myDao();
    }

    /**
     * it gets the data when changed in the db and returns it to the ViewModel
     * @return
     */
    public LiveData<NumberData> getNumberData() {
        return mDBDao.retrieveOneNumber();
    }

    /**
     * called by the UI to request the generation of a new random number
     */
    public void generateNewNumber() {
        Random r = new Random();
        int i1 = r.nextInt(10000 - 1) + 1;
        new insertAsyncTask(mDBDao).execute(new NumberData(i1));
    }

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


    public void savePhoto(List<File> returnedPhotos) {
        myPictureList.addAll(getImageElements(returnedPhotos));
        Log.i("Debug", "savePhoto: Image has been added");
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

}
