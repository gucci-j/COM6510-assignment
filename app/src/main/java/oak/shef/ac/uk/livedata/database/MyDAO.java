/*
 * Copyright (c) 2018. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface MyDAO {
    @Insert
    void insertAll(NumberData... numberData);

    @Insert
    void insert(NumberData numberData);

    @Delete
    void delete(NumberData numberData);

    // it selects a random element
    @Query("SELECT * FROM numberData ORDER BY RANDOM() LIMIT 1")
    LiveData<NumberData> retrieveOneNumber();

    @Delete
    void deleteAll(NumberData... numberData);

    @Query("SELECT COUNT(*) FROM numberData")
    int howManyElements();
}
