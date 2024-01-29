package com.battery.TheftAlerm.utils;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import com.battery.TheftAlerm.R;
import kotlinx.coroutines.DebugKt;

public class FlashlightController {
    private CameraManager camManager;
    private Camera camera;
    private Context context;
    private boolean isFlashOn = false;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private Camera.Parameters params;

    public void getCamera() {
    }

    public FlashlightController(Context context2) {
        this.context = context2;
    }

    private class TurnOffFlashlight extends AsyncTask {
        final FlashlightController this$0;

        public Void doInBackground(Void[] voidArr) {
            return null;
        }

        @Override // android.os.AsyncTask
        public Object doInBackground(Object[] objArr) {
            return doInBackground((Void[]) objArr);
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Object obj) {
            onPostExecute((Void) obj);
        }

        public void onPostExecute(Void r1) {
            super.onPostExecute((Object) r1);
            FlashlightController.this.turnOffFlash();
            FlashlightController.this.release();
        }

        private TurnOffFlashlight() {
            this.this$0 = FlashlightController.this;
        }

        TurnOffFlashlight(FlashlightController flashlightController, FlashlightController flashlightController2, FlashlightController flashlightController3, FlashlightController flashlightController4, TurnOffFlashlight turnOffFlashlight) {
            this();
        }
    }

    private class TurnOnFlashlight extends AsyncTask {
        final FlashlightController this$0;

        @Override // android.os.AsyncTask
        public Object doInBackground(Object[] objArr) {
            return doInBackground((Void[]) objArr);
        }

        public Void doInBackground(Void[] voidArr) {
            FlashlightController.this.getCamera();
            return null;
        }

        @Override // android.os.AsyncTask
        public void onPostExecute(Object obj) {
            onPostExecute((Void) obj);
        }

        public void onPostExecute(Void r1) {
            super.onPostExecute((Object) r1);
            FlashlightController.this.turnOnFlash();
        }

        private TurnOnFlashlight() {
            this.this$0 = FlashlightController.this;
        }

        TurnOnFlashlight(FlashlightController flashlightController, FlashlightController flashlightController2, FlashlightController flashlightController3, FlashlightController flashlightController4, TurnOnFlashlight turnOnFlashlight) {
            this();
        }
    }

    public boolean isFlashOn() {
        return this.isFlashOn;
    }

    public void release() {
        Camera camera2 = this.camera;
        if (camera2 != null) {
            try {
                camera2.stopPreview();
            } catch (Exception unused) {
            }
            this.camera.release();
            this.camera = null;
        }
    }

    public void setFlashOn(boolean z) {
        this.isFlashOn = z;
    }

    public void setFlashlight(boolean z) {
        if (z) {
            setFlashOn(true);
            new TurnOnFlashlight(this, this, this, this, null).execute(new Void[0]);
            return;
        }
        setFlashOn(false);
        new TurnOffFlashlight(this, this, this, this, null).execute(new Void[0]);
    }

    public void turnOffFlash() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Context context2 = this.context;
                CameraManager cameraManager = (CameraManager) context2.getSystemService(context2.getString(R.string.camera));
                this.camManager = cameraManager;
                if (cameraManager != null) {
                    cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], false);
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            Camera open = Camera.open();
            this.mCamera = open;
            Camera.Parameters parameters2 = open.getParameters();
            this.parameters = parameters2;
            parameters2.setFlashMode(DebugKt.DEBUG_PROPERTY_VALUE_OFF);
            this.mCamera.setParameters(this.parameters);
            this.mCamera.stopPreview();
        }
    }

    public void turnOnFlash() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Context context2 = this.context;
                CameraManager cameraManager = (CameraManager) context2.getSystemService(context2.getString(R.string.camera));
                this.camManager = cameraManager;
                if (cameraManager != null) {
                    cameraManager.setTorchMode(cameraManager.getCameraIdList()[0], true);
                }
            } catch (CameraAccessException e) {
                Log.e("TAG", e.toString());
            }
        } else {
            Camera open = Camera.open();
            this.mCamera = open;
            Camera.Parameters parameters2 = open.getParameters();
            this.parameters = parameters2;
            parameters2.setFlashMode("torch");
            this.mCamera.setParameters(this.parameters);
            this.mCamera.startPreview();
        }
    }
}
