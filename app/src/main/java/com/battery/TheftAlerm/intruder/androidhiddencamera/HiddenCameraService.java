package com.battery.TheftAlerm.intruder.androidhiddencamera;

import android.app.Service;
import android.content.Intent;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.core.app.ActivityCompat;

public abstract class HiddenCameraService extends Service implements CameraCallbacks {
    private CameraPreview mCameraPreview;
    private WindowManager mWindowManager;

    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        stopCamera();
    }

    public void onDestroy() {
        super.onDestroy();
        stopCamera();
    }

     public void startCamera(CameraConfig cameraConfig) {
        if (!HiddenCameraUtils.canOverDrawOtherApps(this)) {
            onCameraError(CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION);
        } else if (ActivityCompat.checkSelfPermission(getApplicationContext(), "android.permission.CAMERA") != 0) {
            onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE);
        } else if (cameraConfig.getFacing() != 1 || HiddenCameraUtils.isFrontCameraAvailable(this)) {
            if (this.mCameraPreview == null) {
                this.mCameraPreview = addPreView();
            }
            this.mCameraPreview.startCameraInternal(cameraConfig);
        } else {
            onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA);
        }
    }
     public void takePicture() {
        CameraPreview cameraPreview = this.mCameraPreview;
        if (cameraPreview == null) {
            throw new RuntimeException("Background camera not initialized. Call startCamera() to initialize the camera.");
        } else if (cameraPreview.isSafeToTakePictureInternal()) {
            this.mCameraPreview.takePictureInternal();
        }
    }

     public void stopCamera() {
        CameraPreview cameraPreview = this.mCameraPreview;
        if (cameraPreview != null) {
            this.mWindowManager.removeView(cameraPreview);
            this.mCameraPreview.stopPreviewAndFreeCamera();
        }
    }

    private CameraPreview addPreView() {
        CameraPreview cameraPreview = new CameraPreview(this, this);
        cameraPreview.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        this.mWindowManager = (WindowManager) getSystemService("window");
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2038, 8, -3);
        layoutParams.gravity = 51;
        layoutParams.width = 1;
        layoutParams.height = 1;
        layoutParams.x = 0;
        layoutParams.y = 0;
        this.mWindowManager.addView(cameraPreview, layoutParams);
        return cameraPreview;
    }
}
