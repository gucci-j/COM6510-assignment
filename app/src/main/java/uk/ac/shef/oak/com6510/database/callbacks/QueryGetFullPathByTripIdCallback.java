/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510.database.callbacks;

import java.util.List;

import uk.ac.shef.oak.com6510.database.PhotoData;

public interface QueryGetFullPathByTripIdCallback {
    public void onRetrieveFullPathFinished(String fullpath);
}
