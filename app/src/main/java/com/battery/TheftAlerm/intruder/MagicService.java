package com.battery.TheftAlerm.intruder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.battery.TheftAlerm.R;
import com.battery.TheftAlerm.intruder.androidhiddencamera.CameraConfig;
import com.battery.TheftAlerm.intruder.androidhiddencamera.HiddenCameraService;
import com.battery.TheftAlerm.intruder.androidhiddencamera.HiddenCameraUtils;
import com.battery.TheftAlerm.intruder.androidhiddencamera.config.CameraImageFormat;
import com.battery.TheftAlerm.intruder.androidhiddencamera.config.CameraResolution;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

public class MagicService extends HiddenCameraService {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            Toast.makeText(this, "Camera permission not available", 0).show();
            return 2;
        } else if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
            startCamera(new CameraConfig().getBuilder(this).setCameraFacing(1).setCameraResolution(CameraResolution.MEDIUM_RESOLUTION).setImageFormat(CameraImageFormat.FORMAT_JPEG).setCameraFocus(0).build());
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    MagicService.this.takePicture();
                }
            }, 2000);
            return 2;
        } else {
            HiddenCameraUtils.openDrawOverPermissionSetting(this);
            return 2;
        }
    }

    @Override
    public void onImageCapture(File file) {
        File file2;
        String path = file.getPath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        int i = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        Bitmap decodeFile = BitmapFactory.decodeFile(path, new BitmapFactory.Options());
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String attribute = exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION);
            if (attribute != null) {
                i = Integer.parseInt(attribute);
            }
            if (i == 6) {
                Log.e("ContentValues", "ExifInterface.ORIENTATION_ROTATE_90");
            }
            if (i == 3) {
                Log.e("ContentValues", "ExifInterface.ORIENTATION_ROTATE_180");
            }
            if (i == 8) {
                Log.e("ContentValues", "ExifInterface.ORIENTATION_ROTATE_270");
            }
            exifInterface.saveAttributes();
            Matrix matrix = new Matrix();
            matrix.setRotate(270.0f);
            decodeFile = Bitmap.createBitmap(decodeFile, 0, 0, options.outWidth, options.outHeight, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT <= 29) {
            file2 = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Anti-Theft Alarm System");
            if (!file2.mkdirs()) {
                Log.i("TAG", "Can't create directory to save the image");
            }
        } else {
            file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Anti-Theft Alarm System");
            if (!file2.exists() && !file2.mkdirs()) {
                Log.i("TAG", "Can't create directory to save the image");
            }
        }
        String str = "Image-" + new Random().nextInt(10000) + ".jpg";
        File file3 = new File(file2, str);
        Log.i("path", "" + str);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file3);
            decodeFile.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        stopSelf();
    }

    @Override
    public void onCameraError(int i) {
        if (i == 1122) {
            Toast.makeText(this, (int) R.string.error_cannot_open, 1).show();
        } else if (i == 3136) {
            HiddenCameraUtils.openDrawOverPermissionSetting(this);
        } else if (i == 5472) {
            Toast.makeText(this, (int) R.string.error_cannot_get_permission, 1).show();
        } else if (i == 8722) {
            Toast.makeText(this, (int) R.string.error_not_having_camera, 1).show();
        } else if (i == 9854) {
            Toast.makeText(this, (int) R.string.error_cannot_write, 1).show();
        }
        stopSelf();
    }
}
