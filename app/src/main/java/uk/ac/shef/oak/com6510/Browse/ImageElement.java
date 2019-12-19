/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.Browse;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class ImageElement implements Serializable {
    int image = -1;
    Bitmap file = null;
    Uri uri = null;
    int tripId;
    String timeValue;
    Float pressureValue;
    Float temperatureValue;
    double GPSLatitude;
    double GPSLongitude;
    public ImageElement(Bitmap fileX, Uri uriX,int tripId,Float pressureValue,Float temperatureValue,String time,double GPSLatitude,double GPSLongitude) {
        file = fileX;
        uri = uriX;
        this.tripId=tripId;
        this.pressureValue=pressureValue;
        this.temperatureValue=temperatureValue;
        this.timeValue=time;
        this.GPSLatitude=GPSLatitude;
        this.GPSLongitude=GPSLongitude;
    }
}
