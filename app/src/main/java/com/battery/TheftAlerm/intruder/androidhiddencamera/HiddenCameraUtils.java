package com.battery.TheftAlerm.intruder.androidhiddencamera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.provider.Settings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class HiddenCameraUtils {
    public static boolean canOverDrawOtherApps(Context context) {
        return Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context);
    }

    public static void openDrawOverPermissionSetting(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION");
            intent.addFlags(268435456);
            context.startActivity(intent);
        }
    }

    static File getCacheDir(Context context) {
        return context.getExternalCacheDir() == null ? context.getCacheDir() : context.getExternalCacheDir();
    }

    public static boolean isFrontCameraAvailable(Context context) {
        return Camera.getNumberOfCameras() > 0 && context.getPackageManager().hasSystemFeature("android.hardware.camera.front");
    }

    static Bitmap rotateBitmap(Bitmap bitmap, int i) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) i);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

      static boolean saveImageFromFile(Bitmap bitmap, File file, int i) {
        Bitmap.CompressFormat compressFormat;
        Throwable th;
        boolean z = false;
        Exception e;
        if (i == 563) {
            compressFormat = Bitmap.CompressFormat.WEBP;
        } else if (i != 849) {
            compressFormat = Bitmap.CompressFormat.PNG;
        } else {
            compressFormat = Bitmap.CompressFormat.JPEG;
        }
        FileOutputStream fileOutputStream = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
            try {
                bitmap.compress(compressFormat, 100, fileOutputStream2);
                z = true;
                try {
                    fileOutputStream2.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (Exception e3) {
                e = e3;
                fileOutputStream = fileOutputStream2;
                try {
                    e.printStackTrace();
                    z = false;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    return z;
                } catch (Throwable th2) {
                    th = th2;
                    if (fileOutputStream != null) {
                        try {
                            fileOutputStream.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                    }
                    try {
                        throw th;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream = fileOutputStream2;
                if (fileOutputStream != null) {
                }
                try {
                    throw th;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        } catch (Exception e5) {
            e = e5;
            e.printStackTrace();
            z = false;
            if (fileOutputStream != null) {
            }
            return z;
        }
        return z;
    }
}
