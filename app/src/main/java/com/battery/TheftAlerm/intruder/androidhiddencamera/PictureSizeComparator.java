package com.battery.TheftAlerm.intruder.androidhiddencamera;

import android.hardware.Camera;
import java.util.Comparator;

class PictureSizeComparator implements Comparator<Camera.Size> {
    PictureSizeComparator() {
    }

    public int compare(Camera.Size size, Camera.Size size2) {
        return (size2.height * size2.width) - (size.height * size.width);
    }
}
