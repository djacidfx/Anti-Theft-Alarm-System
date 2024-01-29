package com.battery.TheftAlerm.PocketSensor;

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

public class Pocket_Sensor extends AppCompatActivity implements SensorEventListener {
    private static final int REQUEST_CODE = 123;
    public ImageView active;
    public boolean clicked = false;
    TextView countDown;
    MediaPlayer f109mp;
    public FlashlightController flashlightController;
    int graceTime;
    public boolean isActivited = false;
    AdView mAdView;
    CountDownTimer mCountDownTimer;
    SharedPreferences mPreferences;
    private SensorManager mProxiSensorManager;
    private PowerManager.WakeLock mWakeLock;
    YourPreference preference;
    private Sensor proximitySensor;
    MediaButtonReceiver receiver;
    RippleBackground rippleBackground;
    SharedPreferences sharedflash;
    SharedPreferences sharedvobration;
    public boolean started = false;
    Switch toggle_flash;
    Switch toggle_vibrate;
    TextView txtactivate;
    Vibrator vibrator;

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pocket_sensor);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {

            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.loadAd(new AdRequest.Builder().build());
        this.receiver = new MediaButtonReceiver();
        PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, "My Tag");
        this.mWakeLock = newWakeLock;
        newWakeLock.acquire();
        this.preference = YourPreference.getInstance(this);
        this.vibrator = (Vibrator) getSystemService("vibrator");
        this.txtactivate = (TextView) findViewById(R.id.oo);
        this.rippleBackground = (RippleBackground) findViewById(R.id.content);
        this.mPreferences = getSharedPreferences(Constants_AntiTheft.MYPREFERANCES, 0);
        this.countDown = (TextView) findViewById(R.id.sec_count_down);
        this.active = (ImageView) findViewById(R.id.start_btn);
        this.graceTime = getGraceTime();
        SensorManager sensorManager = (SensorManager) getSystemService("sensor");
        this.mProxiSensorManager = sensorManager;
        this.proximitySensor = sensorManager.getDefaultSensor(8);
        this.toggle_flash = (Switch) findViewById(R.id.toggle_flash_btn);
        this.toggle_vibrate = (Switch) findViewById(R.id.toggle_vibrate_btn);
        this.flashlightController = new FlashlightController(this);
        SharedPreferences sharedPreferences = getSharedPreferences("save_pocket_flash", 0);
        this.sharedflash = sharedPreferences;
        this.toggle_flash.setChecked(sharedPreferences.getBoolean("value_pocket_flash", false));
        SharedPreferences sharedPreferences2 = getSharedPreferences("pocket_save", 0);
        this.sharedvobration = sharedPreferences2;
        this.toggle_vibrate.setChecked(sharedPreferences2.getBoolean("pocket_value", false));
        this.active.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Pocket_Sensor.this.isActivited) {
                    Pocket_Sensor.this.showLock();
                } else if (!Pocket_Sensor.this.clicked) {
                    Pocket_Sensor.this.startCountDown();
                } else {
                    Toast.makeText(Pocket_Sensor.this, "Please wait...", 0).show();
                }
            }
        });
        this.toggle_vibrate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Pocket_Sensor.this.toggle_vibrate.isChecked()) {
                    SharedPreferences.Editor edit = Pocket_Sensor.this.getSharedPreferences("pocket_save", 0).edit();
                    edit.putBoolean("pocket_value", true);
                    edit.apply();
                    Pocket_Sensor.this.toggle_vibrate.setChecked(true);
                    return;
                }
                SharedPreferences.Editor edit2 = Pocket_Sensor.this.getSharedPreferences("pocket_save", 0).edit();
                edit2.putBoolean("pocket_value", false);
                edit2.apply();
                Pocket_Sensor.this.toggle_vibrate.setChecked(false);
            }
        });
        this.toggle_flash.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Pocket_Sensor.this.toggle_flash.isChecked()) {
                    SharedPreferences.Editor edit = Pocket_Sensor.this.getSharedPreferences("save_pocket_flash", 0).edit();
                    edit.putBoolean("value_pocket_flash", true);
                    edit.apply();
                    Pocket_Sensor.this.toggle_flash.setChecked(true);
                    return;
                }
                SharedPreferences.Editor edit2 = Pocket_Sensor.this.getSharedPreferences("save_pocket_flash", 0).edit();
                edit2.putBoolean("value_pocket_flash", false);
                edit2.apply();
                Pocket_Sensor.this.toggle_flash.setChecked(false);
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


    private void showLock() {
        if (isLocked()) {
            startActivityForResult(new Intent(this, PinActivity.class), 123);
        } else {
            deActiveSeneor();
        }
    }

    private int getGraceTime() {
        int i = this.mPreferences.getInt(Constants_AntiTheft.GRACE_TIME, 0);
        if (i != 1) {
            return i != 2 ? 5000 : 15000;
        }
        return 10000;
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
        this.countDown.setText("Activate");
        this.txtactivate.setText("Tap To Activate");
        this.active.setImageResource(R.drawable.activate);
        this.rippleBackground.stopRippleAnimation();
    }

    public void startCountDown() {
        this.mCountDownTimer = new CountDownTimer((long) this.graceTime, 1000) {

            public void onTick(long j) {
                Pocket_Sensor.this.rippleBackground.startRippleAnimation();
                Pocket_Sensor.this.clicked = true;
                Pocket_Sensor.this.started = true;
                Pocket_Sensor.this.countDown.setVisibility(0);
                TextView textView = Pocket_Sensor.this.countDown;
                textView.setText("00:" + (j / 1000));
            }

            public void onFinish() {
                Pocket_Sensor.this.clicked = false;
                Pocket_Sensor.this.isActivited = true;
                Pocket_Sensor.this.countDown.setText("Stop");
                Pocket_Sensor.this.txtactivate.setText("Tap To Deactivate");
                Pocket_Sensor.this.countDown.setTextColor(SupportMenu.CATEGORY_MASK);
                Pocket_Sensor.this.active.setImageResource(R.drawable.activate);
            }
        }.start();
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        this.mProxiSensorManager.registerListener(this, this.proximitySensor, 3);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mProxiSensorManager.unregisterListener(this);
        this.mWakeLock.release();
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 8 && this.isActivited) {
            ((AudioManager) getSystemService("audio")).setStreamVolume(3, ((AudioManager) getSystemService("audio")).getStreamMaxVolume(3), 0);
            this.f109mp.start();
            this.f109mp.setLooping(true);
            flashlight();
            Vibrator();
        }
        Vibrator();
    }

    @Override
    public void onBackPressed() {
        if (this.started) {
            Toast.makeText(this, "Pocket sensor is active", 0).show();
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

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            deActiveSeneor();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 24 || keyCode == 25) {
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }
}
