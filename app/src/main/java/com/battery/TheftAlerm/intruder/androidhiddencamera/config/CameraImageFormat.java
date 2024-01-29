package com.battery.TheftAlerm.intruder.androidhiddencamera.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class CameraImageFormat {
    public static final int FORMAT_JPEG = 849;
    public static final int FORMAT_PNG = 545;
    public static final int FORMAT_WEBP = 563;

    @Retention(RetentionPolicy.SOURCE)
    public @interface SupportedImageFormat {
    }

    private CameraImageFormat() {
        throw new RuntimeException("Cannot initialize CameraImageFormat.");
    }
}
