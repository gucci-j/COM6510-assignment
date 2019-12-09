/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.Serializable;

public class ImageElement implements Serializable {
    int image = -1;
    Bitmap file = null;
    Uri uri = null;

    public ImageElement(int image) {
        this.image = image;
    }

    public ImageElement(Bitmap fileX, Uri uriX) {
        file = fileX;
        uri = uriX;
    }
}
