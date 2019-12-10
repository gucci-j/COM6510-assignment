/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata.database;

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
     * Desc: "onConflict=OnConflictStrategy.REPLACE" is important for updating data.
     *        If the same image is uploaded, the entry of the image will be replaced
     *        with the new data.
     * Ref: https://developer.android.com/reference/android/arch/persistence/room/OnConflictStrategy
     * @param photoData
     */
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(PhotoData photoData);

    @Query("SELECT * from photo_table ORDER BY time DESC")
    LiveData<List<PhotoData>> getAllPhotos();

    // Delete, Edit, Retrieve group by
}
