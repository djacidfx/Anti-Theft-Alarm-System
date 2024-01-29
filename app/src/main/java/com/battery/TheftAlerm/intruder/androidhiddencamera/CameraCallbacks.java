package com.battery.TheftAlerm.intruder.androidhiddencamera;

import java.io.File;

/* access modifiers changed from: package-private */
public interface CameraCallbacks {
    void onCameraError(int i);

    void onImageCapture(File file);
}
