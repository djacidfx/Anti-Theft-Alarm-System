package com.battery.TheftAlerm.intruder.androidhiddencamera;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public abstract class HiddenCameraActivity extends AppCompatActivity implements CameraCallbacks {
    private CameraConfig mCachedCameraConfig;
    private CameraPreview mCameraPreview;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mCameraPreview = addPreView();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        stopCamera();
    }

    /* access modifiers changed from: protected */
    public void startCamera(CameraConfig cameraConfig) {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE);
        } else if (cameraConfig.getFacing() != 1 || HiddenCameraUtils.isFrontCameraAvailable(this)) {
            this.mCachedCameraConfig = cameraConfig;
            this.mCameraPreview.startCameraInternal(cameraConfig);
        } else {
            onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA);
        }
    }

    /* access modifiers changed from: protected */
    public void takePicture() {
        CameraPreview cameraPreview = this.mCameraPreview;
        if (cameraPreview == null) {
            throw new RuntimeException("Background camera not initialized. Call startCamera() to initialize the camera.");
        } else if (cameraPreview.isSafeToTakePictureInternal()) {
            this.mCameraPreview.takePictureInternal();
        }
    }

    /* access modifiers changed from: protected */
    public void stopCamera() {
        this.mCachedCameraConfig = null;
        CameraPreview cameraPreview = this.mCameraPreview;
        if (cameraPreview != null) {
            cameraPreview.stopPreviewAndFreeCamera();
        }
    }

    private CameraPreview addPreView() {
        CameraPreview cameraPreview = new CameraPreview(this, this);
        cameraPreview.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        View childAt = ((ViewGroup) getWindow().getDecorView().getRootView()).getChildAt(0);
        if (childAt instanceof LinearLayout) {
            ((LinearLayout) childAt).addView(cameraPreview, new LinearLayout.LayoutParams(1, 1));
        } else if (childAt instanceof RelativeLayout) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(1, 1);
            layoutParams.addRule(9, -1);
            layoutParams.addRule(12, -1);
            ((RelativeLayout) childAt).addView(cameraPreview, layoutParams);
        } else if (childAt instanceof FrameLayout) {
            ((FrameLayout) childAt).addView(cameraPreview, new FrameLayout.LayoutParams(1, 1));
        } else {
            throw new RuntimeException("Root view of the activity/fragment cannot be other than Linear/Relative/Frame layout");
        }
        return cameraPreview;
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        if (this.mCachedCameraConfig != null && ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") == 0) {
            startCamera(this.mCachedCameraConfig);
        }
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        CameraPreview cameraPreview = this.mCameraPreview;
        if (cameraPreview != null) {
            cameraPreview.stopPreviewAndFreeCamera();
        }
    }
}
