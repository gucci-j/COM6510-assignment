/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PhotoDAO {

    /**
     * insert
     * Desc: With this method, we can insert a photo data.
     *      "onConflict=OnConflictStrategy.REPLACE" is important for updating data in case of duplication.
     * Ref: https://developer.android.com/reference/android/arch/persistence/room/OnConflictStrategy
     * @param photoData
     */
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(PhotoData photoData);

    // Get images sorted by ASC
    @Query("SELECT * from photo_table ORDER BY time ASC")
    LiveData<List<PhotoData>> getAllPhotos();

    // Get images with a specified trip ID using LiveData
    @Query("SELECT * from photo_table WHERE trip_id = :tripId")
    LiveData<List<PhotoData>> getTripPhotos(int tripId);

    // Get images with a specified tripID without using LiveData.
    // To use this method, we must implement the aSync task.
    @Query("SELECT * from photo_table WHERE trip_id = :tripId")
    List<PhotoData> getTripPhotosASync(int tripId);

    // Delete, Edit, Retrieve group
}
