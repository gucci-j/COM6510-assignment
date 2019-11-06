/*
 * Copyright (c) 2018. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Random;

import oak.shef.ac.uk.livedata.database.MyDAO;
import oak.shef.ac.uk.livedata.database.MyRoomDatabase;
import oak.shef.ac.uk.livedata.database.NumberData;

class MyRepository extends ViewModel {
    private final MyDAO mDBDao;

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
}
