package com.battery.TheftAlerm.intruder.androidhiddencamera;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public abstract class HiddenCameraFragment extends Fragment implements CameraCallbacks {
    private CameraConfig mCachedCameraConfig;
    private CameraPreview mCameraPreview;

     public void startCamera(CameraConfig cameraConfig) {
        if (ActivityCompat.checkSelfPermission(getActivity(), "android.permission.CAMERA") != 0) {
            onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE);
        } else if (cameraConfig.getFacing() != 1 || HiddenCameraUtils.isFrontCameraAvailable(getActivity())) {
            if (this.mCameraPreview == null) {
                this.mCameraPreview = addPreView();
            }
            this.mCameraPreview.startCameraInternal(cameraConfig);
            this.mCachedCameraConfig = cameraConfig;
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
        this.mCachedCameraConfig = null;
        CameraPreview cameraPreview = this.mCameraPreview;
        if (cameraPreview != null) {
            cameraPreview.stopPreviewAndFreeCamera();
        }
    }

    private CameraPreview addPreView() {
        CameraPreview cameraPreview = new CameraPreview(getActivity(), this);
        cameraPreview.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        View childAt = ((ViewGroup) getActivity().getWindow().getDecorView().getRootView()).getChildAt(0);
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
            throw new RuntimeException("Root view of the activity/fragment cannot be other than Linear/Relative or frame layout");
        }
        return cameraPreview;
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mCachedCameraConfig != null && ActivityCompat.checkSelfPermission(getContext(), "android.permission.CAMERA") == 0) {
            startCamera(this.mCachedCameraConfig);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        CameraPreview cameraPreview = this.mCameraPreview;
        if (cameraPreview != null) {
            cameraPreview.stopPreviewAndFreeCamera();
        }
    }
}
