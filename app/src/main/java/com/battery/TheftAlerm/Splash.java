package com.battery.TheftAlerm;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    private static final long COUNTER_TIME = 3;
    private long secondsRemaining;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        createTimer(COUNTER_TIME);
    }

    private void createTimer(long j) {
        new CountDownTimer(j * 1000, 1000) {

            public void onTick(long j) {
                Splash.this.secondsRemaining = (j / 1000) + 1;
            }

            public void onFinish() {
                Splash.this.secondsRemaining = 0;
                Application application = Splash.this.getApplication();
                if (!(application instanceof MyApplication)) {
                    Splash.this.startMainActivity();
                } else {
                    ((MyApplication) application).showAdIfAvailable(Splash.this, new MyApplication.OnShowAdCompleteListener() {

                        @Override
                        public void onShowAdComplete() {
                            Splash.this.startMainActivity();
                        }
                    });
                }
            }
        }.start();
    }

    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
