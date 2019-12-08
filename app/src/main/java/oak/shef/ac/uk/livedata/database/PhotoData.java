/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Entity for each photo
 * Ref: https://developer.android.com/reference/android/arch/persistence/room/ForeignKey.html#childColumns()
 *      Lecture slide (Week 5 Persisting Data)
 */
// @Entity(tableName = "photo_table", foreignKeys = @ForeignKey(entity = TripData.class, parentColumns = "id", childColumns = "trip_id"))

@Entity(tableName = "photo_table")
public class PhotoData {
    @PrimaryKey
    @android.support.annotation.NonNull
    private String filename; // Filename for the photo

    /*
    @ColumnInfo(name = "trip_id")
    private int tripId; // Id from TripData
     */
    private String time; // The time when a photo was taken
    // Add here for GPS & sensor_data


    public PhotoData(String filename, String time) {
        this.filename = filename;
        //this.tripId = tripId;
        this.time = time;
    }

    @NonNull
    public String getFilename() {
        return filename;
    }

    /*
    public int getTripId() {
        return tripId;
    }
     */

    public String getTime() {
        return time;
    }
}
