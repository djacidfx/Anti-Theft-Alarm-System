package com.battery.TheftAlerm.intruder.androidhiddencamera.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class CameraFacing {
    public static final int FRONT_FACING_CAMERA = 1;
    public static final int REAR_FACING_CAMERA = 0;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SupportedCameraFacing {
    }

    private CameraFacing() {
        throw new RuntimeException("Cannot initialize this class.");
    }
}
