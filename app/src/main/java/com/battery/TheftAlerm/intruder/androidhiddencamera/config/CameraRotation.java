package com.battery.TheftAlerm.intruder.androidhiddencamera.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class CameraRotation {
    public static final int ROTATION_0 = 0;
    public static final int ROTATION_180 = 180;
    public static final int ROTATION_270 = 270;
    public static final int ROTATION_90 = 90;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SupportedRotation {
    }

    private CameraRotation() {
        throw new RuntimeException("Cannot initialize this class.");
    }
}
