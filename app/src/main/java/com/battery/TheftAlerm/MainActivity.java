package com.battery.TheftAlerm;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.battery.TheftAlerm.HeadphoneAlerm.HeadPhone;
import com.battery.TheftAlerm.MotionSensor.Motion_Sensor;
import com.battery.TheftAlerm.PocketSensor.Pocket_Sensor;
import com.battery.TheftAlerm.chargedetect.ChargerDetection;
import com.battery.TheftAlerm.FullBatteryHealth.FullBattery_Alarm;
import com.battery.TheftAlerm.intruder.Intruder_Alert;
import com.battery.TheftAlerm.utils.Constants_AntiTheft;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

public class MainActivity extends AppCompatActivity {
    CardView AdCard;
    RelativeLayout AntiPocket;
    RelativeLayout Charging_Detction;
    RelativeLayout Earphone_Detection;
    RelativeLayout FullBatteryDetection;
    RelativeLayout MoreApps;
    RelativeLayout Motion_Detction;
    RelativeLayout RateUs;
    RelativeLayout Settings;
    RelativeLayout WifiDetection;
    LinearLayout Adlayout1;
    SharedPreferences.Editor editor;
    PowerManager.WakeLock f110wl;
    LinearLayout layout_no;
    LinearLayout layout_yes;
    LinearLayout layoutrateus;
    AdView mAdView;
    private InterstitialAd mInterstitialAd;
    SharedPreferences mPreferences;
    private NativeAd nativeAd;
    TextView textView;

     @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {


            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        Interstitialad();
        nativead();
        this.AdCard = (CardView) findViewById(R.id.adcard);
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            this.AdCard.setVisibility(8);
        } else {
            this.AdCard.setVisibility(0);
        }
        PowerManager.WakeLock newWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(1, getClass().getName());
        this.f110wl = newWakeLock;
        newWakeLock.acquire();
        SharedPreferences sharedPreferences = getSharedPreferences(Constants_AntiTheft.MYPREFERANCES, 0);
        this.mPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
        this.textView = (TextView) findViewById(R.id.txt);
        if (this.mPreferences.getInt(Constants_AntiTheft.DEFAULT_VOLUME, 0) == 0) {
            this.editor.putInt(Constants_AntiTheft.DEFAULT_VOLUME, 15);
            this.editor.commit();
        }
        this.Motion_Detction = (RelativeLayout) findViewById(R.id.motionBtn);
        this.AntiPocket = (RelativeLayout) findViewById(R.id.pocketBtn);
        this.Charging_Detction = (RelativeLayout) findViewById(R.id.chargingBtn);
        this.Earphone_Detection = (RelativeLayout) findViewById(R.id.earphoneBtn);
        this.WifiDetection = (RelativeLayout) findViewById(R.id.wifiBtn);
        this.FullBatteryDetection = (RelativeLayout) findViewById(R.id.fullbatryBtn);
        this.MoreApps = (RelativeLayout) findViewById(R.id.moreappsBtn);
        this.RateUs = (RelativeLayout) findViewById(R.id.rateusBtn);
        this.Settings = (RelativeLayout) findViewById(R.id.settings);
        this.Motion_Detction.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, Motion_Sensor.class));
            }
        });
        this.AntiPocket.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (MainActivity.this.mInterstitialAd != null) {
                    MainActivity.this.mInterstitialAd.show(MainActivity.this);
                    MainActivity.this.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            MainActivity.this.startActivity(new Intent(MainActivity.this, Pocket_Sensor.class));
                            MainActivity.this.Interstitialad();
                        }
                    });
                    return;
                }
                MainActivity.this.startActivity(new Intent(MainActivity.this, Pocket_Sensor.class));
            }
        });
        this.Charging_Detction.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, ChargerDetection.class));
            }
        });
        this.Earphone_Detection.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (MainActivity.this.mInterstitialAd != null) {
                    MainActivity.this.mInterstitialAd.show(MainActivity.this);
                    MainActivity.this.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                        @Override // com.google.android.gms.ads.FullScreenContentCallback
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            MainActivity.this.startActivity(new Intent(MainActivity.this, HeadPhone.class));
                            MainActivity.this.Interstitialad();
                        }
                    });
                    return;
                }
                MainActivity.this.startActivity(new Intent(MainActivity.this, HeadPhone.class));
            }
        });
        this.WifiDetection.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (MainActivity.this.mInterstitialAd != null) {
                    MainActivity.this.mInterstitialAd.show(MainActivity.this);
                    MainActivity.this.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            MainActivity.this.startActivity(new Intent(MainActivity.this, Intruder_Alert.class));
                            MainActivity.this.Interstitialad();
                        }
                    });
                    return;
                }
                MainActivity.this.startActivity(new Intent(MainActivity.this, Intruder_Alert.class));
            }
        });
        this.FullBatteryDetection.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (MainActivity.this.mInterstitialAd != null) {
                    MainActivity.this.mInterstitialAd.show(MainActivity.this);
                    MainActivity.this.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            MainActivity.this.startActivity(new Intent(MainActivity.this, FullBattery_Alarm.class));
                            MainActivity.this.Interstitialad();
                        }
                    });
                    return;
                }
                MainActivity.this.startActivity(new Intent(MainActivity.this, FullBattery_Alarm.class));
            }
        });
        this.MoreApps.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("https://play.google.com/store/apps/dev?id=8599109884308816488"));
                MainActivity.this.startActivity(intent);
            }
        });
        this.RateUs.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                MainActivity mainActivity = MainActivity.this;
                mainActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
            }
        });
        this.Settings.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (MainActivity.this.mInterstitialAd != null) {
                    MainActivity.this.mInterstitialAd.show(MainActivity.this);
                    MainActivity.this.mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                        @Override // com.google.android.gms.ads.FullScreenContentCallback
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            MainActivity.this.startActivity(new Intent(MainActivity.this, Settings.class));
                            MainActivity.this.Interstitialad();
                        }
                    });
                    return;
                }
                MainActivity.this.startActivity(new Intent(MainActivity.this, Settings.class));
            }
        });
    }

    private void Interstitialad() {
        InterstitialAd.load(this, getString(R.string.interstitial), new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {

            public void onAdLoaded(InterstitialAd interstitialAd) {
                MainActivity.this.mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                MainActivity.this.mInterstitialAd = null;
            }
        });
    }

    @Override
    public void onBackPressed() {
        onexit();
    }

    public void onexit() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.exit_dilogee, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflate);
        final AlertDialog create = builder.create();
        this.Adlayout1 = (LinearLayout) inflate.findViewById(R.id.adexit);
        this.mAdView = (AdView) inflate.findViewById(R.id.adView);
        this.mAdView.loadAd(new AdRequest.Builder().build());
        this.layout_no = (LinearLayout) inflate.findViewById(R.id.layout_no);
        this.layoutrateus = (LinearLayout) inflate.findViewById(R.id.layoutrateus);
        this.layout_yes = (LinearLayout) inflate.findViewById(R.id.layout_yes);
        this.layout_no.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                create.dismiss();
            }
        });
        this.layoutrateus.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse("market://details?id=" + MainActivity.this.getPackageName()));
                MainActivity.this.startActivity(intent);
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://market.android.com/details?id=" + MainActivity.this.getPackageName())));
                create.dismiss();
            }
        });
        this.layout_yes.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                create.dismiss();
                MainActivity.this.moveTaskToBack(true);
            }
        });
        create.getWindow().setLayout(-2, -2);
        create.show();
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

            @Override // com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
            public void onNativeAdLoaded(NativeAd nativeAd) {
                if (MainActivity.this.nativeAd != null) {
                    MainActivity.this.nativeAd.destroy();
                }
                MainActivity.this.nativeAd = nativeAd;
                FrameLayout frameLayout = (FrameLayout) MainActivity.this.findViewById(R.id.fl_adplaceholderMain);
                NativeAdView nativeAdView = (NativeAdView) MainActivity.this.getLayoutInflater().inflate(R.layout.mediation_native_ad, (ViewGroup) null);
                MainActivity.this.populateNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
                MainActivity.this.textView.setVisibility(8);
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
