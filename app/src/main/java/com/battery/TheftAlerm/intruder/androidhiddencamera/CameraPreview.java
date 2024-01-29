package com.battery.TheftAlerm.intruder.androidhiddencamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

 public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera mCamera;
    private CameraCallbacks mCameraCallbacks;
    private CameraConfig mCameraConfig;
    private SurfaceHolder mHolder;
    private volatile boolean safeToTakePicture = false;

     public void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
    }

    CameraPreview(Context context, CameraCallbacks cameraCallbacks) {
        super(context);
        this.mCameraCallbacks = cameraCallbacks;
        initSurfaceView();
    }

    private void initSurfaceView() {
        SurfaceHolder holder = getHolder();
        this.mHolder = holder;
        holder.addCallback(this);
        this.mHolder.setType(3);
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Camera.Size size;
        if (this.mCamera == null) {
            this.mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        } else if (surfaceHolder.getSurface() == null) {
            this.mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        } else {
            try {
                this.mCamera.stopPreview();
            } catch (Exception unused) {
            }
            Camera.Parameters parameters = this.mCamera.getParameters();
            List<Camera.Size> supportedPictureSizes = this.mCamera.getParameters().getSupportedPictureSizes();
            Collections.sort(supportedPictureSizes, new PictureSizeComparator());
            int resolution = this.mCameraConfig.getResolution();
            if (resolution == 2006) {
                size = supportedPictureSizes.get(0);
            } else if (resolution == 7821) {
                size = supportedPictureSizes.get(supportedPictureSizes.size() - 1);
            } else if (resolution == 7895) {
                size = supportedPictureSizes.get(supportedPictureSizes.size() / 2);
            } else {
                throw new RuntimeException("Invalid camera resolution.");
            }
            parameters.setPictureSize(size.width, size.height);
            if (parameters.getSupportedFocusModes().contains(this.mCameraConfig.getFocusMode())) {
                parameters.setFocusMode(this.mCameraConfig.getFocusMode());
            }
            requestLayout();
            this.mCamera.setParameters(parameters);
            try {
                this.mCamera.setDisplayOrientation(90);
                this.mCamera.setPreviewDisplay(surfaceHolder);
                this.mCamera.setPreviewDisplay(surfaceHolder);
                this.mCamera.startPreview();
                this.safeToTakePicture = true;
            } catch (IOException | NullPointerException unused2) {
                this.mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Camera camera = this.mCamera;
        if (camera != null) {
            camera.stopPreview();
        }
    }

    /* access modifiers changed from: package-private */
    public void startCameraInternal(CameraConfig cameraConfig) {
        this.mCameraConfig = cameraConfig;
        if (!safeCameraOpen(cameraConfig.getFacing())) {
            this.mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        } else if (this.mCamera != null) {
            requestLayout();
            try {
                this.mCamera.setPreviewDisplay(this.mHolder);
                this.mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
                this.mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
            }
        }
    }

    private boolean safeCameraOpen(int i) {
        try {
            stopPreviewAndFreeCamera();
            Camera open = Camera.open(i);
            this.mCamera = open;
            if (open != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("CameraPreview", "failed to open Camera");
            e.printStackTrace();
            return false;
        }
    }

     public boolean isSafeToTakePictureInternal() {
        return this.safeToTakePicture;
    }

     public void takePictureInternal() {
        this.safeToTakePicture = false;
        Camera camera = this.mCamera;
        if (camera != null) {
            camera.takePicture(null, null, new Camera.PictureCallback() {

                public void onPictureTaken(final byte[] bArr, Camera camera) {
                    new Thread(new Runnable() {


                        public void run() {
                            Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
                            if (CameraPreview.this.mCameraConfig.getImageRotation() != 0) {
                                decodeByteArray = HiddenCameraUtils.rotateBitmap(decodeByteArray, CameraPreview.this.mCameraConfig.getImageRotation());
                            }
                            if (HiddenCameraUtils.saveImageFromFile(decodeByteArray, CameraPreview.this.mCameraConfig.getImageFile(), CameraPreview.this.mCameraConfig.getImageFormat())) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {

                                    public void run() {
                                        CameraPreview.this.mCameraCallbacks.onImageCapture(CameraPreview.this.mCameraConfig.getImageFile());
                                    }
                                });
                            } else {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {


                                    public void run() {
                                        CameraPreview.this.mCameraCallbacks.onCameraError(CameraError.ERROR_IMAGE_WRITE_FAILED);
                                    }
                                });
                            }
                            CameraPreview.this.mCamera.startPreview();
                            CameraPreview.this.safeToTakePicture = true;
                        }
                    }).start();
                }
            });
            return;
        }
        this.mCameraCallbacks.onCameraError(CameraError.ERROR_CAMERA_OPEN_FAILED);
        this.safeToTakePicture = true;
    }

     public void stopPreviewAndFreeCamera() {
        this.safeToTakePicture = false;
        Camera camera = this.mCamera;
        if (camera != null) {
            camera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }
}
