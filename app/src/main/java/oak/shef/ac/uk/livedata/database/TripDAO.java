/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Data Access Objects for Trip data
 * Ref: https://developer.android.com/training/data-storage/room/accessing-data
 */
@Dao
public interface TripDAO {
    @Insert
    void insert(TripData tripData);

    @Query("SELECT * from trip_table ORDER BY id DESC")
    LiveData<List<TripData>> getAllTrips();

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
