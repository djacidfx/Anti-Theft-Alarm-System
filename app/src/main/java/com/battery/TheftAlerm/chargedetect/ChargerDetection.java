package com.battery.TheftAlerm.chargedetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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

public class ChargerDetection extends AppCompatActivity {
    private static final int REQUEST_CODE = 123;
    public ImageView active;
    int chargerFlag;
    int chargerFlag1;
    int chargerFlag2 = 0;
    public boolean clicked = false;
    TextView countDown;
    MediaPlayer f109mp;
    public FlashlightController flashlightController;
    int graceTime;
    public boolean isActivited = false;
    AdView mAdView;
    CountDownTimer mCountDownTimer;
    SharedPreferences mPreferences;
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

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_charger_detection);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {

            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        this.mAdView = (AdView) findViewById(R.id.adView);
        this.mAdView.loadAd(new AdRequest.Builder().build());
        this.receiver = new MediaButtonReceiver();
        this.preference = YourPreference.getInstance(this);
        this.mPreferences = getSharedPreferences(Constants_AntiTheft.MYPREFERANCES, 0);
        this.rippleBackground = (RippleBackground) findViewById(R.id.content);
        this.txtactivate = (TextView) findViewById(R.id.oo);
        this.countDown = (TextView) findViewById(R.id.sec_count_down);
        this.active = (ImageView) findViewById(R.id.start_btn);
        this.graceTime = getGraceTime();
        this.vibrator = (Vibrator) getSystemService("vibrator");
        this.toggle_flash = (Switch) findViewById(R.id.toggle_flash_btn);
        this.toggle_vibrate = (Switch) findViewById(R.id.toggle_vibrate_btn);
        this.flashlightController = new FlashlightController(this);
        SharedPreferences sharedPreferences = getSharedPreferences("save_charger_flash", 0);
        this.sharedflash = sharedPreferences;
        this.toggle_flash.setChecked(sharedPreferences.getBoolean("value_charger_flash", false));
        SharedPreferences sharedPreferences2 = getSharedPreferences("save_charger_vibrate", 0);
        this.sharedvobration = sharedPreferences2;
        this.toggle_vibrate.setChecked(sharedPreferences2.getBoolean("value_charger_vibrate", false));
        registerReceiver(new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                int intExtra = intent.getIntExtra("plugged", -1);
                if (intExtra == 1 || intExtra == 2) {
                    ChargerDetection.this.chargerFlag = 1;
                } else if (intExtra == 0) {
                    ChargerDetection.this.chargerFlag1 = 1;
                    ChargerDetection.this.chargerFlag = 0;
                    ChargerDetection.this.myFunction();
                    ChargerDetection.this.flashlight();
                    ChargerDetection.this.Vibrator();
                }
            }
        }, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        this.active.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (ChargerDetection.this.isActivited) {
                    ChargerDetection.this.showLock();
                } else if (!ChargerDetection.this.clicked) {
                    ChargerDetection.this.checkCharger();
                } else {
                    Toast.makeText(ChargerDetection.this, "Please wait...", 0).show();
                }
            }
        });
        this.toggle_vibrate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (ChargerDetection.this.toggle_vibrate.isChecked()) {
                    SharedPreferences.Editor edit = ChargerDetection.this.getSharedPreferences("save_charger_vibrate", 0).edit();
                    edit.putBoolean("value_charger_vibrate", true);
                    edit.apply();
                    ChargerDetection.this.toggle_vibrate.setChecked(true);
                    return;
                }
                SharedPreferences.Editor edit2 = ChargerDetection.this.getSharedPreferences("save_charger_vibrate", 0).edit();
                edit2.putBoolean("value_charger_vibrate", false);
                edit2.apply();
                ChargerDetection.this.toggle_vibrate.setChecked(false);
            }
        });
        this.toggle_flash.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (ChargerDetection.this.toggle_flash.isChecked()) {
                    SharedPreferences.Editor edit = ChargerDetection.this.getSharedPreferences("save_charger_flash", 0).edit();
                    edit.putBoolean("value_charger_flash", true);
                    edit.apply();
                    ChargerDetection.this.toggle_flash.setChecked(true);
                    return;
                }
                SharedPreferences.Editor edit2 = ChargerDetection.this.getSharedPreferences("save_charger_flash", 0).edit();
                edit2.putBoolean("value_charger_flash", false);
                edit2.apply();
                ChargerDetection.this.toggle_flash.setChecked(false);
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


    private void showLock() {
        if (isLocked()) {
            startActivityForResult(new Intent(this, PinActivity.class), 123);
        } else {
            deActiveSeneor();
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

    public void deActiveSeneor() {
        this.isActivited = false;
        this.started = false;
        if (this.flashlightController.isFlashOn()) {
            this.flashlightController.setFlashlight(false);
        }
        this.vibrator.cancel();
        if (this.f109mp.isPlaying()) {
            this.f109mp.pause();
        }
        this.countDown.setTextColor(getResources().getColor(R.color.purple_700));
        this.countDown.setText("Activate");
        this.txtactivate.setText("Tap To Activate");
        this.rippleBackground.stopRippleAnimation();
        this.active.setImageResource(R.drawable.activate);
    }

    private void startCountDown() {
        this.mCountDownTimer = new CountDownTimer((long) this.graceTime, 1000) {

            public void onTick(long j) {
                ChargerDetection.this.rippleBackground.startRippleAnimation();
                ChargerDetection.this.clicked = true;
                ChargerDetection.this.started = true;
                ChargerDetection.this.countDown.setVisibility(0);
                TextView textView = ChargerDetection.this.countDown;
                textView.setText("00:" + (j / 1000));
            }

            public void onFinish() {
                ChargerDetection.this.clicked = false;
                ChargerDetection.this.isActivited = true;
                Toast.makeText(ChargerDetection.this, "Charger detector Activiated", 0).show();
                ChargerDetection.this.started = true;
                ChargerDetection.this.countDown.setText("Stop");
                ChargerDetection.this.txtactivate.setText("Tap To Deactivate");
                ChargerDetection.this.countDown.setTextColor(SupportMenu.CATEGORY_MASK);
                ChargerDetection.this.active.setImageResource(R.drawable.activate);
                ChargerDetection.this.chargerFlag2 = 1;
            }
        }.start();
    }

    public void checkCharger() {
        if (this.isActivited) {
            this.chargerFlag2 = 0;
        } else if (this.chargerFlag != 1) {
            Toast.makeText(this, "Connect To Charger", 0).show();
        } else {
            startCountDown();
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

    public void myFunction() {
        if (this.isActivited && this.chargerFlag1 == 1 && this.chargerFlag2 == 1) {
            ((AudioManager) getSystemService("audio")).setStreamVolume(3, ((AudioManager) getSystemService("audio")).getStreamMaxVolume(3), 0);
            this.f109mp.start();
            this.f109mp.setLooping(true);
            this.chargerFlag2 = 0;
        }
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        if (this.started) {
            Toast.makeText(this, "Charger detector is active", 0).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            deActiveSeneor();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
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
