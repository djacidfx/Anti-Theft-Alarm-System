package com.battery.TheftAlerm.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.skyfishjy.library.RippleBackground;

public class Wifi_Detection extends AppCompatActivity {
     public ImageView active;
     public boolean clicked = false;
    TextView countDown;
    MediaPlayer f109mp;
    public FlashlightController flashlightController;
    int graceTime;
    boolean isActivited = false;

    CountDownTimer mCountDownTimer;
    SharedPreferences mPreferences;
    YourPreference preference;
    MediaButtonReceiver receiver;
    RelativeLayout relativeLayout;
    RippleBackground rippleBackground;
    SharedPreferences sharedflash;
    SharedPreferences sharedvobration;
    public boolean started = false;
    Switch toggle_flash;
    Switch toggle_vibrate;
    Vibrator vibrator;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_wifi_detection);
        this.receiver = new MediaButtonReceiver();
        this.preference = YourPreference.getInstance(this);
        this.vibrator = (Vibrator) getSystemService("vibrator");
        this.rippleBackground = (RippleBackground) findViewById(R.id.content);
        this.mPreferences = getSharedPreferences(Constants_AntiTheft.MYPREFERANCES, 0);
        this.countDown = (TextView) findViewById(R.id.sec_count_down);
        this.active = (ImageView) findViewById(R.id.start_btn);
        this.graceTime = getGraceTime();
        this.toggle_flash = (Switch) findViewById(R.id.toggle_flash_btn);
        this.toggle_vibrate = (Switch) findViewById(R.id.toggle_vibrate_btn);
        this.flashlightController = new FlashlightController(this);
        SharedPreferences sharedPreferences = getSharedPreferences("save_wifi_flash", 0);
        this.sharedflash = sharedPreferences;
        this.toggle_flash.setChecked(sharedPreferences.getBoolean("value_wifi_flash", false));
        SharedPreferences sharedPreferences2 = getSharedPreferences("save_wifi_vibrate", 0);
        this.sharedvobration = sharedPreferences2;
        this.toggle_vibrate.setChecked(sharedPreferences2.getBoolean("value_wifi_vibrate", false));
        this.active.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Wifi_Detection.this.isActivited) {
                    Wifi_Detection.this.showLock();
                } else if (!Wifi_Detection.this.clicked) {
                    Wifi_Detection.this.startCountDown();
                } else {
                    Toast.makeText(Wifi_Detection.this, "Please wait...", 0).show();
                }
            }
        });
        this.toggle_vibrate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Wifi_Detection.this.toggle_vibrate.isChecked()) {
                    SharedPreferences.Editor edit = Wifi_Detection.this.getSharedPreferences("save_wifi_vibrate", 0).edit();
                    edit.putBoolean("value_wifi_vibrate", true);
                    edit.apply();
                    Wifi_Detection.this.toggle_vibrate.setChecked(true);
                    return;
                }
                SharedPreferences.Editor edit2 = Wifi_Detection.this.getSharedPreferences("save_wifi_vibrate", 0).edit();
                edit2.putBoolean("value_wifi_vibrate", false);
                edit2.apply();
                Wifi_Detection.this.toggle_vibrate.setChecked(false);
            }
        });
        this.toggle_flash.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Wifi_Detection.this.toggle_flash.isChecked()) {
                    SharedPreferences.Editor edit = Wifi_Detection.this.getSharedPreferences("save_wifi_flash", 0).edit();
                    edit.putBoolean("value_wifi_flash", true);
                    edit.apply();
                    Wifi_Detection.this.toggle_flash.setChecked(true);
                    return;
                }
                SharedPreferences.Editor edit2 = Wifi_Detection.this.getSharedPreferences("save_wifi_flash", 0).edit();
                edit2.putBoolean("value_wifi_flash", false);
                edit2.apply();
                Wifi_Detection.this.toggle_flash.setChecked(false);
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

    private int getGraceTime() {
        int i = this.mPreferences.getInt(Constants_AntiTheft.GRACE_TIME, 0);
        if (i != 1) {
            return i != 2 ? 5000 : 15000;
        }
        return 10000;
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
        if (this.flashlightController.isFlashOn()) {
            this.flashlightController.setFlashlight(false);
        }
        this.vibrator.cancel();
        if (this.f109mp.isPlaying()) {
            this.f109mp.pause();
        }
        this.countDown.setTextColor(getResources().getColor(R.color.purple_700));
        this.countDown.setText("Activate");
        this.rippleBackground.stopRippleAnimation();
        this.active.setImageResource(R.drawable.activate);
    }

    private boolean isLocked() {
        return this.preference.get_sw(Constants_AntiTheft.PIN_SWITCH);
    }

    public void startCountDown() {
        this.mCountDownTimer = new CountDownTimer((long) this.graceTime, 1000) {

            public void onTick(long j) {
                Wifi_Detection.this.rippleBackground.startRippleAnimation();
                Wifi_Detection.this.clicked = true;
                Wifi_Detection.this.started = true;
                Wifi_Detection.this.countDown.setVisibility(0);
                TextView textView = Wifi_Detection.this.countDown;
                textView.setText("00:" + (j / 1000));
            }

            public void onFinish() {
                Wifi_Detection.this.clicked = false;
                Wifi_Detection.this.isActivited = true;
                Wifi_Detection.this.countDown.setText("Stop");
                Wifi_Detection.this.countDown.setTextColor(SupportMenu.CATEGORY_MASK);
                Wifi_Detection.this.active.setImageResource(R.drawable.activate);
                WifiBroadcastReceiver wifiBroadcastReceiver = new WifiBroadcastReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
                Wifi_Detection.this.registerReceiver(wifiBroadcastReceiver, intentFilter);
            }
        }.start();
    }

    public void flashlight() {
        if (this.isActivited && this.toggle_flash.isChecked()) {
            this.flashlightController.setFlashlight(true);
        }
    }



    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            deActiveSeneor();
        }
    }

    private class WifiBroadcastReceiver extends BroadcastReceiver {
        private WifiBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
            int intExtra = intent.getIntExtra("wifi_state", -1);
            if (intExtra == 1) {
                Toast.makeText(Wifi_Detection.this, "Disable", 1).show();
            } else if (intExtra == 3) {
                Toast.makeText(Wifi_Detection.this, "Enable", 1).show();
            }
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

    @Override
    public void onBackPressed() {
        if (this.started) {
            Toast.makeText(this, "Wifi_Detection sensor is active", 0).show();
        } else {
            super.onBackPressed();
        }
    }
}
