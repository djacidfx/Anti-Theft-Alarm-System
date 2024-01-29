package com.battery.TheftAlerm.intruder.androidhiddencamera.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class CameraFocus {
    public static final int AUTO = 0;
    public static final int CONTINUOUS_PICTURE = 1;
    public static final int NO_FOCUS = 2;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SupportedCameraFocus {
    }

    private CameraFocus() {
        throw new RuntimeException("Cannot initialize this class.");
    }
}
