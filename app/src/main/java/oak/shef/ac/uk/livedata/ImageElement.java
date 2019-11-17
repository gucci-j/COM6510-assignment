/*
 * Copyright (c) 2019. This code has been developed by Atsuki Yamaguchi, Mingshuo Zhang, and Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package oak.shef.ac.uk.livedata;

import java.io.File;

public class ImageElement {
    int image=-1;
    File file=null;

    public ImageElement(int image) {
        this.image = image;
    }

    public ImageElement(File fileX) {
        file = fileX;
    }
}
