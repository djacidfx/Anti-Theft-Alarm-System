package com.battery.TheftAlerm.intruder.androidhiddencamera;

import android.content.Context;
import com.battery.TheftAlerm.intruder.androidhiddencamera.config.CameraImageFormat;
import com.battery.TheftAlerm.intruder.androidhiddencamera.config.CameraResolution;
import java.io.File;
import kotlinx.coroutines.DebugKt;

public final class CameraConfig {
    private int mCameraFocus = 0;
    private Context mContext;
    private int mFacing = 0;
    private File mImageFile;
    private int mImageFormat = CameraImageFormat.FORMAT_JPEG;
    private int mImageRotation = 0;
    private int mResolution = CameraResolution.MEDIUM_RESOLUTION;

    public Builder getBuilder(Context context) {
        this.mContext = context;
        return new Builder();
    }

     public int getResolution() {
        return this.mResolution;
    }

     public String getFocusMode() {
        int i = this.mCameraFocus;
        if (i == 0) {
            return DebugKt.DEBUG_PROPERTY_VALUE_AUTO;
        }
        if (i == 1) {
            return "continuous-picture";
        }
        if (i == 2) {
            return null;
        }
        throw new RuntimeException("Invalid camera focus mode.");
    }
     public int getFacing() {
        return this.mFacing;
    }

     public int getImageFormat() {
        return this.mImageFormat;
    }

     public File getImageFile() {
        return this.mImageFile;
    }

     public int getImageRotation() {
        return this.mImageRotation;
    }

    public class Builder {
        public Builder() {
        }

        public Builder setCameraResolution(int i) {
            if (i == 2006 || i == 7895 || i == 7821) {
                CameraConfig.this.mResolution = i;
                return this;
            }
            throw new RuntimeException("Invalid camera resolution.");
        }

        public Builder setCameraFacing(int i) {
            if (i == 0 || i == 1) {
                CameraConfig.this.mFacing = i;
                return this;
            }
            throw new RuntimeException("Invalid camera facing value.");
        }

        public Builder setCameraFocus(int i) {
            if (i == 0 || i == 1 || i == 2) {
                CameraConfig.this.mCameraFocus = i;
                return this;
            }
            throw new RuntimeException("Invalid camera focus mode.");
        }

        public Builder setImageFormat(int i) {
            if (i == 849 || i == 545) {
                CameraConfig.this.mImageFormat = i;
                return this;
            }
            throw new RuntimeException("Invalid output image format.");
        }

        public Builder setImageRotation(int i) {
            if (i == 0 || i == 90 || i == 180 || i == 270) {
                CameraConfig.this.mImageRotation = i;
                return this;
            }
            throw new RuntimeException("Invalid image rotation.");
        }

        public Builder setImageFile(File file) {
            CameraConfig.this.mImageFile = file;
            return this;
        }

        public CameraConfig build() {
            if (CameraConfig.this.mImageFile == null) {
                CameraConfig.this.mImageFile = getDefaultStorageFile();
            }
            return CameraConfig.this;
        }

        private File getDefaultStorageFile() {
            StringBuilder sb = new StringBuilder();
            sb.append(HiddenCameraUtils.getCacheDir(CameraConfig.this.mContext).getAbsolutePath());
            sb.append(File.separator);
            sb.append("IMG_");
            sb.append(System.currentTimeMillis());
            sb.append(CameraConfig.this.mImageFormat == 849 ? ".jpeg" : ".png");
            return new File(sb.toString());
        }
    }
}
