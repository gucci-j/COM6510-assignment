/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Objects for Trip data
 * Ref: https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
public interface TripDAO {
    @Insert
    long insert(TripData tripData);

    @Query("SELECT * from trip_table ORDER BY id DESC")
    LiveData<List<TripData>> getAllTrips();

    @Query("SELECT * from trip_table WHERE title = :titleTrip AND date = :dateTrip LIMIT 1")
    TripData getTrip(String titleTrip, String dateTrip);

    @Query("SELECT title from trip_table WHERE id = :tripId LIMIT 1")
    String getTripTitle(int tripId);

    @Query("UPDATE trip_table SET fullpath = :fullPath WHERE id = :tripId")
    void update(int tripId, String fullPath);

    @Query("SELECT fullpath from trip_table WHERE id = :tripId LIMIT 1")
    String getFullPath(int tripId);
    // Can add delete and update?

    /*
    @Insert
    void insertAll(NumberData... numberData);

    @Delete
    void delete(NumberData numberData);

    // it selects a random element
    @Query("SELECT * FROM numberData ORDER BY RANDOM() LIMIT 1")
    LiveData<NumberData> retrieveOneNumber();

    @Delete
    void deleteAll(NumberData... numberData);

    @Query("SELECT COUNT(*) FROM numberData")
    int howManyElements();
     */
}
