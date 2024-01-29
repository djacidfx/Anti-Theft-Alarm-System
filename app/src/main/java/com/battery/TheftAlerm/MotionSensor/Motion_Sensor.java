package com.battery.TheftAlerm.MotionSensor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.internal.view.SupportMenu;
import com.battery.TheftAlerm.MediaButtonReceiver;
import com.battery.TheftAlerm.PinActivity;
import com.battery.TheftAlerm.Preferences.YourPreference;
import com.battery.TheftAlerm.R;
import com.battery.TheftAlerm.utils.Constants_AntiTheft;
import com.battery.TheftAlerm.utils.FlashlightController;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.skyfishjy.library.RippleBackground;

public class Motion_Sensor extends AppCompatActivity implements SensorEventListener {
    private static final int REQUEST_CODE = 123;
    private Sensor Motion_Sensor;
    public ImageView active;
    public boolean clicked = false;
    TextView countDown;
    MediaPlayer f109mp;
    public FlashlightController flashlightController;
    int graceTime;
    public boolean isActivited = false;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private AdView mAdView;
    CountDownTimer mCountDownTimer;
    private float[] mGravity;
    private SensorManager mMotion_SensorManager;
    SharedPreferences mPreferences;
    private PowerManager.WakeLock mWakeLock;
    MediaPlayer mediaPlayer;
    YourPreference preference;
    MediaButtonReceiver receiver;
    RippleBackground rippleBackground;
    SharedPreferences sharedflash;
    SharedPreferences sharedvobration;
    public boolean started = false;
    Switch toggle_flash;
    Switch toggle_vibrate;
    TextView txtactivate;
    Vibrator vibrator;
    int volumeLevel;
    public int which = 0;

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_motion_sensor);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {

            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.loadAd(new AdRequest.Builder().build());
        PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "My Tag");
        this.mWakeLock = newWakeLock;
        newWakeLock.acquire();
        this.receiver = new MediaButtonReceiver();
        this.preference = YourPreference.getInstance(this);
        this.mPreferences = getSharedPreferences(Constants_AntiTheft.MYPREFERANCES, 0);
        this.countDown = (TextView) findViewById(R.id.sec_count_down);
        this.txtactivate = (TextView) findViewById(R.id.oo);
        this.rippleBackground = (RippleBackground) findViewById(R.id.content);
        this.graceTime = getGraceTime();
        this.active = (ImageView) findViewById(R.id.start_btn);
        this.mAccel = 0.0f;
        this.mAccelCurrent = 9.80665f;
        this.mAccelLast = 9.80665f;
        SensorManager sensorManager = (SensorManager) getSystemService("sensor");
        this.mMotion_SensorManager = sensorManager;
        this.Motion_Sensor = sensorManager.getDefaultSensor(1);
        this.vibrator = (Vibrator) getSystemService("vibrator");
        this.toggle_flash = (Switch) findViewById(R.id.toggle_flash_btn);
        this.toggle_vibrate = (Switch) findViewById(R.id.toggle_vibrate_btn);
        this.flashlightController = new FlashlightController(this);
        SharedPreferences sharedPreferences = getSharedPreferences("save_motion_flash", 0);
        this.sharedflash = sharedPreferences;
        this.toggle_flash.setChecked(sharedPreferences.getBoolean("value_motion_flash", false));
        SharedPreferences sharedPreferences2 = getSharedPreferences("save_motion_vibrate", 0);
        this.sharedvobration = sharedPreferences2;
        this.toggle_vibrate.setChecked(sharedPreferences2.getBoolean("value_motion_vibrate", false));
        this.txtactivate.setText("Tap To Activate");
        this.active.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Motion_Sensor.this.isActivited) {
                    Motion_Sensor.this.showLock();
                } else if (!Motion_Sensor.this.clicked) {
                    Motion_Sensor.this.startCountDown();
                } else {
                    Toast.makeText(Motion_Sensor.this, "Please wait...", 0).show();
                }
            }
        });
        this.toggle_vibrate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Motion_Sensor.this.toggle_vibrate.isChecked()) {
                    SharedPreferences.Editor edit = Motion_Sensor.this.getSharedPreferences("save_motion_vibrate", 0).edit();
                    edit.putBoolean("value_motion_vibrate", true);
                    edit.apply();
                    Motion_Sensor.this.toggle_vibrate.setChecked(true);
                    return;
                }
                SharedPreferences.Editor edit2 = Motion_Sensor.this.getSharedPreferences("save_motion_vibrate", 0).edit();
                edit2.putBoolean("value_motion_vibrate", false);
                edit2.apply();
                Motion_Sensor.this.toggle_vibrate.setChecked(false);
            }
        });
        this.toggle_flash.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Motion_Sensor.this.toggle_flash.isChecked()) {
                    SharedPreferences.Editor edit = Motion_Sensor.this.getSharedPreferences("save_motion_flash", 0).edit();
                    edit.putBoolean("value_motion_flash", true);
                    edit.apply();
                    Motion_Sensor.this.toggle_flash.setChecked(true);
                    return;
                }
                SharedPreferences.Editor edit2 = Motion_Sensor.this.getSharedPreferences("save_motion_flash", 0).edit();
                edit2.putBoolean("value_motion_flash", false);
                edit2.apply();
                Motion_Sensor.this.toggle_flash.setChecked(false);
            }
        });
        if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 0) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_5_antitheft);
        } else if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 1) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_4_antitheft);
        } else if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 2) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_3_antitheft);
        } else if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 3) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_1_antitheft);
        } else if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 4) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_2_antitheft);
        } else if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 5) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_6_antitheft);
        } else if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 6) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_7_antitheft);
        } else if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 7) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_8_antitheft);
        } else if (this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0) == 8) {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_9_antitheft);
        } else {
            this.f109mp = MediaPlayer.create(this, (int) R.raw.tone_8_antitheft);
        }
    }

    private boolean isLocked() {
        return this.preference.get_sw(Constants_AntiTheft.PIN_SWITCH);
    }

    private int getGraceTime() {
        int i = this.mPreferences.getInt(Constants_AntiTheft.GRACE_TIME, 0);
        if (i != 1) {
            return i != 2 ? 5000 : 15000;
        }
        return 10000;
    }

    private boolean isintruderon() {
        return this.preference.get_sw(Constants_AntiTheft.INTRUDER_SWITCH);
    }

    public void startCountDown() {
        this.mCountDownTimer = new CountDownTimer((long) this.graceTime, 1000) {

            public void onTick(long j) {
                Motion_Sensor.this.rippleBackground.startRippleAnimation();
                Motion_Sensor.this.active.setImageResource(R.drawable.activate);
                Motion_Sensor.this.started = true;
                Motion_Sensor.this.clicked = true;
                Motion_Sensor.this.countDown.setVisibility(0);
                TextView textView = Motion_Sensor.this.countDown;
                textView.setText("00:" + (j / 1000));
            }

            public void onFinish() {
                Motion_Sensor.this.clicked = false;
                Motion_Sensor.this.isActivited = true;
                Motion_Sensor.this.countDown.setText("Stop");
                Motion_Sensor.this.txtactivate.setText("Tap to Deactivate");
                Motion_Sensor.this.countDown.setTextColor(SupportMenu.CATEGORY_MASK);
                Motion_Sensor.this.active.setImageResource(R.drawable.stop);
            }
        }.start();
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        this.mMotion_SensorManager.registerListener(this, this.Motion_Sensor, 2);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
        this.mMotion_SensorManager.unregisterListener(this);
        this.mWakeLock.release();
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 1) {
            float[] fArr = (float[]) sensorEvent.values.clone();
            this.mGravity = fArr;
            float f = fArr[0];
            float f2 = fArr[1];
            float f3 = fArr[2];
            this.mAccelLast = this.mAccelCurrent;
            float sqrt = (float) Math.sqrt((double) ((f * f) + (f2 * f2) + (f3 * f3)));
            this.mAccelCurrent = sqrt;
            float f4 = (this.mAccel * 0.9f) + (sqrt - this.mAccelLast);
            this.mAccel = f4;
            if (this.isActivited && ((double) f4) > 0.7d) {
                ((AudioManager) getSystemService("audio")).setStreamVolume(3, ((AudioManager) getSystemService("audio")).getStreamMaxVolume(3), 0);
                this.f109mp.start();
                this.f109mp.setLooping(true);
                flashlight();
                Vibrator();
            }
        }
    }


    private void showLock() {
        if (isLocked()) {
            startActivityForResult(new Intent(this, PinActivity.class), 123);
        } else {
            deActiveSeneor();
        }
    }

    public void deActiveSeneor() {
        this.isActivited = false;
        this.started = false;
        this.f109mp.pause();
        if (this.flashlightController.isFlashOn()) {
            this.flashlightController.setFlashlight(false);
        }
        this.vibrator.cancel();
        this.countDown.setTextColor(getResources().getColor(R.color.purple_700));
        this.txtactivate.setText("Tap To Activate");
        this.countDown.setText("Activate");
        this.active.setImageResource(R.drawable.activate);
        this.rippleBackground.stopRippleAnimation();
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        if (this.started) {
            Toast.makeText(this, "Motion sensor is active", 0).show();
        } else {
            super.onBackPressed();
        }
    }

    public void flashlight() {
        if (this.isActivited && this.toggle_flash.isChecked()) {
            this.flashlightController.setFlashlight(true);
        }
    }

    public void Vibrator() {
        if ((this.isActivited && this.toggle_vibrate.isChecked()) && Build.VERSION.SDK_INT >= 26) {
            this.vibrator.vibrate(new long[]{60, 120, 180, 240, 300, 360, 420, 480}, 1);
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            deActiveSeneor();
        }
    }

    @Override // androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 24 || keyCode == 25) {
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }
}
