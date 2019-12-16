/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.database;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Entity for each photo
 * Ref: https://developer.android.com/reference/android/arch/persistence/room/ForeignKey.html#childColumns()
 *      Lecture slide (Week 5 Persisting Data)
 */
//@Entity(tableName = "photo_table")
@Entity(tableName = "photo_table", foreignKeys = @ForeignKey(entity = TripData.class, parentColumns = "id", childColumns = "trip_id"))
public class PhotoData {
    @PrimaryKey
    @NonNull
    private String filename; // Filename for the photo: Uri

    @ColumnInfo(name = "trip_id")
    private int tripId; // Id from TripData

    private String time; // The time when a photo was taken

    // Sensor Data
    private Float pressureValue;
    private Float temperatureValue;

    // GPS Data
    private double GPSLatitude;
    private double GPSLongitude;

    public PhotoData(String filename, int tripId, String time, Float pressureValue, Float temperatureValue, double GPSLatitude, double GPSLongitude) {
        this.filename = filename;
        this.tripId = tripId;
        this.time = time;
        this.pressureValue = pressureValue;
        this.temperatureValue = temperatureValue;
        this.GPSLatitude = GPSLatitude;
        this.GPSLongitude = GPSLongitude;
    }

    @NonNull
    public String getFilename() {
        return filename;
    }

    public int getTripId() {
        return tripId;
    }

    public String getTime() {
        return time;
    }

    public Float getPressureValue() {
        return pressureValue;
    }

    public Float getTemperatureValue() {
        return temperatureValue;
    }

    public double getGPSLatitude() { return GPSLatitude; }
    public double getGPSLongitude() { return GPSLongitude; }
}
