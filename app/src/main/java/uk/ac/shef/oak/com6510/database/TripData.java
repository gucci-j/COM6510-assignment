/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "trip_table")
public class TripData {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id = 0; // SQLite automatically increments id
    private String title; // Title of a visit
    private String date; // Start date of the visit

    @ColumnInfo(name = "fullpath")
    private String fullpath; // Full path of the trip


    public TripData(String title, String date) {
        this.title = title;
        this.date = date;
        this.fullpath=null;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }


    @NonNull
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getFullpath() {
        return fullpath;
    }
    public void setFullpath(String fullpath){
        this.fullpath=fullpath;
    }
}
