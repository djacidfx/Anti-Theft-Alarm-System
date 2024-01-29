package com.battery.TheftAlerm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.battery.TheftAlerm.Preferences.YourPreference;
import com.battery.TheftAlerm.utils.Constants_AntiTheft;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class Settings extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 123;
    public static final String TAG = "Set";
    RelativeLayout Laynative;
    ShimmerFrameLayout NativeShimmer;
    Actions action;
    int adCounter = 0;
    private LinearLayout adView;
    LinearLayout changePin;
    LinearLayout changeTime;
    LinearLayout changeTone;
    LinearLayout changeVolume;
    SharedPreferences.Editor editor;
    FrameLayout frameLayout;
    LinearLayout helpBtn;
    MediaPlayer mMediaPlayer;
    SharedPreferences mPreferences;
    NativeAd nativeAd;
    Switch onOff_Switch;
    String[] options;
    YourPreference preference;
    TextView result;
    LinearLayout seekBarContaincer;
    int seekBarVolumeLevel;
    int selectedTimeOption;
    int selectedToneOption;
    int selectedVolumeOption;
    RelativeLayout shimmer;
    int toneNumber;
    SeekBar volumeSeekBar;

    enum Actions {
        DisablePIN,
        ChangePIN
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);
        nativead();
        this.Laynative = (RelativeLayout) findViewById(R.id.Laynative);
        this.NativeShimmer = (ShimmerFrameLayout) findViewById(R.id.shimmer_container_Native);
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            this.NativeShimmer.setVisibility(8);
            this.NativeShimmer.stopShimmer();
        } else {
            this.NativeShimmer.setVisibility(0);
            this.NativeShimmer.startShimmer();
        }
        this.preference = YourPreference.getInstance(this);
        this.onOff_Switch = (Switch) findViewById(R.id.onoff_Switch);
        this.options = new String[]{"5 Seconds", "10 Seconds", "15 Seconds", "20 Seconds"};
        this.changeTone = (LinearLayout) findViewById(R.id.selectTone_linearLayout);
        this.changeTime = (LinearLayout) findViewById(R.id.change_time);
        this.changePin = (LinearLayout) findViewById(R.id.change_pin_linearLayout);
        this.helpBtn = (LinearLayout) findViewById(R.id.help);
        this.volumeSeekBar = (SeekBar) findViewById(R.id.volume_seekbar);
        this.seekBarContaincer = (LinearLayout) findViewById(R.id.seekbar_container);
        this.changeTone.setOnClickListener(this);
        this.changePin.setOnClickListener(this);
        this.changeTime.setOnClickListener(this);
        this.helpBtn.setOnClickListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants_AntiTheft.MYPREFERANCES, 0);
        this.mPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
        this.selectedTimeOption = this.mPreferences.getInt(Constants_AntiTheft.GRACE_TIME, 0);
        this.selectedVolumeOption = this.mPreferences.getInt(Constants_AntiTheft.DEFAULT_VOLUME, 0);
        this.onOff_Switch.setChecked(this.preference.get_sw(Constants_AntiTheft.PIN_SWITCH));
        this.onOff_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                Settings.this.onOffSwitch(z);
            }
        });
        this.onOff_Switch.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                if (!Settings.this.onOff_Switch.isChecked()) {
                    Settings.this.action = Actions.DisablePIN;
                    Settings.this.requestPIN();
                } else if (!Settings.this.preference.getFirstTimePIN(Constants_AntiTheft.FIRST_TIME_PIN)) {
                    Settings.this.requestPIN();
                    Settings.this.preference.setFirstTimePIN(Constants_AntiTheft.FIRST_TIME_PIN, true);
                }
            }
        });
    }


    private void onOffSwitch(boolean z) {
        this.preference.set_sw(Constants_AntiTheft.PIN_SWITCH, z);
        Toast.makeText(this, z ? "PIN Activated" : "PIN Deactivating", 0).show();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.help) {
            switch (id) {
                case R.id.change_pin_linearLayout:
                    this.action = Actions.ChangePIN;
                    changePIN();
                    return;
                case R.id.change_time:
                    changeTimeDialog();
                    return;
                case R.id.selectTone_linearLayout:
                    changeToneDialog();
                    return;
                default:
                    return;
            }
        }
    }

    private void changePIN() {
        if (isLocked()) {
            requestPIN();
        } else {
            setPIN();
        }
    }


    private void requestPIN() {
        startActivityForResult(new Intent(this, PinActivity.class), 123);
    }

    public void setPIN() {
        startActivity(PinActivity.getIntent(this, true));
    }

    private void changeTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Grace Time");
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.pref_time_name), this.selectedTimeOption, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                Settings.this.editor.putInt(Constants_AntiTheft.GRACE_TIME, i);
                Settings.this.editor.commit();
                Settings settings = Settings.this;
                settings.selectedTimeOption = settings.mPreferences.getInt(Constants_AntiTheft.GRACE_TIME, 0);
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void changeToneDialog() {
        this.selectedToneOption = this.mPreferences.getInt(Constants_AntiTheft.ALERT_TONE, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Alert Tone");
        builder.setCancelable(false);
        this.mMediaPlayer = new MediaPlayer();
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                Settings.this.editor.putInt(Constants_AntiTheft.ALERT_TONE, Settings.this.toneNumber);
                Settings.this.editor.commit();
                if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                    Settings.this.mMediaPlayer.stop();
                    Settings.this.mMediaPlayer = null;
                }
            }
        });
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.pref_tone_name), this.selectedToneOption, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings = Settings.this;
                    settings.mMediaPlayer = MediaPlayer.create(settings, (int) R.raw.tone_5_antitheft);
                    Settings.this.mMediaPlayer.start();
                } else if (i == 1) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings2 = Settings.this;
                    settings2.mMediaPlayer = MediaPlayer.create(settings2, (int) R.raw.tone_4_antitheft);
                    Settings.this.mMediaPlayer.start();
                } else if (i == 2) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings3 = Settings.this;
                    settings3.mMediaPlayer = MediaPlayer.create(settings3, (int) R.raw.tone_3_antitheft);
                    Settings.this.mMediaPlayer.start();
                } else if (i == 3) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings4 = Settings.this;
                    settings4.mMediaPlayer = MediaPlayer.create(settings4, (int) R.raw.tone_1_antitheft);
                    Settings.this.mMediaPlayer.start();
                } else if (i == 4) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings5 = Settings.this;
                    settings5.mMediaPlayer = MediaPlayer.create(settings5, (int) R.raw.tone_2_antitheft);
                    Settings.this.mMediaPlayer.start();
                } else if (i == 5) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings6 = Settings.this;
                    settings6.mMediaPlayer = MediaPlayer.create(settings6, (int) R.raw.tone_6_antitheft);
                    Settings.this.mMediaPlayer.start();
                } else if (i == 6) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings7 = Settings.this;
                    settings7.mMediaPlayer = MediaPlayer.create(settings7, (int) R.raw.tone_7_antitheft);
                    Settings.this.mMediaPlayer.start();
                } else if (i == 7) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings8 = Settings.this;
                    settings8.mMediaPlayer = MediaPlayer.create(settings8, (int) R.raw.tone_8_antitheft);
                    Settings.this.mMediaPlayer.start();
                } else if (i == 8) {
                    Settings.this.toneNumber = i;
                    if (Settings.this.mMediaPlayer.isPlaying() || Settings.this.mMediaPlayer != null) {
                        Settings.this.mMediaPlayer.stop();
                        Settings.this.mMediaPlayer = null;
                    }
                    Settings settings9 = Settings.this;
                    settings9.mMediaPlayer = MediaPlayer.create(settings9, (int) R.raw.tone_9_antitheft);
                    Settings.this.mMediaPlayer.start();
                }
            }
        });
        builder.create().show();
    }

    public boolean isLocked() {
        return this.preference.get_sw(Constants_AntiTheft.PIN_SWITCH);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 != -1) {
            if (i2 == 1) {
                this.onOff_Switch.setChecked(true);
            }
        } else if (this.action == Actions.ChangePIN) {
            setPIN();
        } else if (this.action == Actions.DisablePIN) {
            Toast.makeText(this, "PIN Deactivated", 0).show();
        }
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onDestroy() {
        super.onDestroy();
    }

    public void populateNativeAdView(NativeAd nativeAd2, NativeAdView nativeAdView) {
        nativeAdView.setMediaView((MediaView) nativeAdView.findViewById(R.id.ad_media));
        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
        nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
        nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd2.getHeadline());
        if (nativeAd2.getBody() == null) {
            nativeAdView.getBodyView().setVisibility(4);
        } else {
            nativeAdView.getBodyView().setVisibility(0);
            ((TextView) nativeAdView.getBodyView()).setText(nativeAd2.getBody());
        }
        if (nativeAd2.getCallToAction() == null) {
            nativeAdView.getCallToActionView().setVisibility(4);
        } else {
            nativeAdView.getCallToActionView().setVisibility(0);
            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd2.getCallToAction());
        }
        if (nativeAd2.getIcon() == null) {
            nativeAdView.getIconView().setVisibility(8);
        } else {
            ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd2.getIcon().getDrawable());
            nativeAdView.getIconView().setVisibility(0);
        }
        if (nativeAd2.getPrice() == null) {
            nativeAdView.getPriceView().setVisibility(4);
        } else {
            nativeAdView.getPriceView().setVisibility(0);
            ((TextView) nativeAdView.getPriceView()).setText(nativeAd2.getPrice());
        }
        if (nativeAd2.getStore() == null) {
            nativeAdView.getStoreView().setVisibility(4);
        } else {
            nativeAdView.getStoreView().setVisibility(0);
            ((TextView) nativeAdView.getStoreView()).setText(nativeAd2.getStore());
        }
        if (nativeAd2.getStarRating() == null || nativeAd2.getStarRating().doubleValue() < 3.0d) {
            nativeAdView.getStarRatingView().setVisibility(4);
        } else {
            ((RatingBar) nativeAdView.getStarRatingView()).setRating(nativeAd2.getStarRating().floatValue());
            nativeAdView.getStarRatingView().setVisibility(0);
        }
        if (nativeAd2.getAdvertiser() == null) {
            nativeAdView.getAdvertiserView().setVisibility(4);
        } else {
            ((TextView) nativeAdView.getAdvertiserView()).setText(nativeAd2.getAdvertiser());
            nativeAdView.getAdvertiserView().setVisibility(0);
        }
        nativeAdView.setNativeAd(nativeAd2);
    }

    private void nativead() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.native_advanced));
        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {

            @Override
            public void onNativeAdLoaded(NativeAd nativeAd) {
                if (Settings.this.nativeAd != null) {
                    Settings.this.nativeAd.destroy();
                }
                Settings.this.nativeAd = nativeAd;
                FrameLayout frameLayout = (FrameLayout) Settings.this.findViewById(R.id.fl_adplaceholderMain);
                NativeAdView nativeAdView = (NativeAdView) Settings.this.getLayoutInflater().inflate(R.layout.mediation_native_ad, (ViewGroup) null);
                Settings.this.populateNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
                Settings.this.NativeShimmer.setVisibility(8);
                Settings.this.NativeShimmer.stopShimmer();
                frameLayout.setVisibility(0);
            }
        }).build();
        builder.withNativeAdOptions(new NativeAdOptions.Builder().build());
        builder.withAdListener(new AdListener() {

            public void onAdFailedToLoad(int i) {
            }
        }).build().loadAd(new AdRequest.Builder().build());
    }
}
