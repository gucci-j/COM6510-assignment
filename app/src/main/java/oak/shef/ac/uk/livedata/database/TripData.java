/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity(tableName = "trip_table")
public class TripData {
    @PrimaryKey(autoGenerate = true)
    @android.support.annotation.NonNull
    @ColumnInfo(name = "id")
    private int id = 0; // SQLite automatically increments id
    private String title; // Title of a visit
    private String date; // Start date of the visit


    public TripData(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public void setId(@android.support.annotation.NonNull int id) {
        this.id = id;
    }


    @android.support.annotation.NonNull
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }
}
