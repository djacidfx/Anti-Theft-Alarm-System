package com.battery.TheftAlerm.intruder;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.battery.TheftAlerm.Preferences.YourPreference;
import com.battery.TheftAlerm.R;
import com.battery.TheftAlerm.utils.Constants_AntiTheft;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.nativead.NativeAd;

public class Intruder_Alert extends AppCompatActivity {
    static final int ACTIVATION_REQUEST = 47;
    CardView Intrduerselfies;
    RelativeLayout Laynative;
    ShimmerFrameLayout NativeShimmer;
    SharedPreferences.Editor editor;
    boolean firstRun;
    Switch intruder_onoff;
    NativeAd nativeAd;
    SharedPreferences pref;
    YourPreference preference;
    Button yes;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_intruder_alert);
//        nativead();
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
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS_NAME", 0);
        this.pref = sharedPreferences;
        this.editor = sharedPreferences.edit();
        boolean z = this.pref.getBoolean("firstRun", true);
        this.firstRun = z;
        if (z) {
            Log.i("onCreate: ", "first time");
            this.editor.putBoolean("firstRun", false);
            this.editor.commit();
            dialoge();
        }
        if (!this.firstRun) {
            checkRunTimePermission();
        }
        this.intruder_onoff = (Switch) findViewById(R.id.intruder_onoff);
        this.Intrduerselfies = (CardView) findViewById(R.id.intruderphotos);
        YourPreference instance = YourPreference.getInstance(this);
        this.preference = instance;
        this.intruder_onoff.setChecked(instance.get_swintruder(Constants_AntiTheft.INTRUDER_SWITCH));
        this.intruder_onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {


            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                Intruder_Alert.this.onOff_Switch(z);
            }
        });
        this.Intrduerselfies.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                Intruder_Alert.this.startActivity(new Intent(Intruder_Alert.this, CollectionsActivity.class));
            }
        });
    }


    private void onOff_Switch(boolean z) {
        this.preference.set_swintruder(Constants_AntiTheft.INTRUDER_SWITCH, z);
        Toast.makeText(this, z ? "Activated" : "Deactivating", 0).show();
    }

    private void showLock() {
        isLocked();
    }

    private boolean isLocked() {
        return this.preference.get_sw(Constants_AntiTheft.PIN_SWITCH);
    }

    public void dialoge() {
        View inflate = LayoutInflater.from(this).inflate(R.layout.exit_diloge, (ViewGroup) null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflate);
        final AlertDialog create = builder.create();
        create.setCanceledOnTouchOutside(false);
        Button button = (Button) inflate.findViewById(R.id.contin);
        this.yes = button;
        button.setOnClickListener(new View.OnClickListener() {


            public void onClick(View view) {
                try {
                    Intruder_Alert.this.checkRunTimePermission();
                } catch (Exception unused) {
                }
                create.dismiss();
            }
        });
        create.getWindow().setLayout(-2, -2);
        create.show();
    }


    private void checkRunTimePermission() {
        String[] strArr = {"android.permission.CAMERA", "android.permission.SYSTEM_ALERT_WINDOW"};
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(strArr, 11111);
        }
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse("package:" + getPackageName())), 101);
        }
    }

//    public void populateNativeAdView(NativeAd nativeAd2, NativeAdView nativeAdView) {
//        nativeAdView.setMediaView((MediaView) nativeAdView.findViewById(R.id.ad_media));
//        nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
//        nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
//        nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
//        nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
//        nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
//        nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
//        nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
//        nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
//        ((TextView) nativeAdView.getHeadlineView()).setText(nativeAd2.getHeadline());
//        if (nativeAd2.getBody() == null) {
//            nativeAdView.getBodyView().setVisibility(4);
//        } else {
//            nativeAdView.getBodyView().setVisibility(0);
//            ((TextView) nativeAdView.getBodyView()).setText(nativeAd2.getBody());
//        }
//        if (nativeAd2.getCallToAction() == null) {
//            nativeAdView.getCallToActionView().setVisibility(4);
//        } else {
//            nativeAdView.getCallToActionView().setVisibility(0);
//            ((Button) nativeAdView.getCallToActionView()).setText(nativeAd2.getCallToAction());
//        }
//        if (nativeAd2.getIcon() == null) {
//            nativeAdView.getIconView().setVisibility(8);
//        } else {
//            ((ImageView) nativeAdView.getIconView()).setImageDrawable(nativeAd2.getIcon().getDrawable());
//            nativeAdView.getIconView().setVisibility(0);
//        }
//        if (nativeAd2.getPrice() == null) {
//            nativeAdView.getPriceView().setVisibility(4);
//        } else {
//            nativeAdView.getPriceView().setVisibility(0);
//            ((TextView) nativeAdView.getPriceView()).setText(nativeAd2.getPrice());
//        }
//        if (nativeAd2.getStore() == null) {
//            nativeAdView.getStoreView().setVisibility(4);
//        } else {
//            nativeAdView.getStoreView().setVisibility(0);
//            ((TextView) nativeAdView.getStoreView()).setText(nativeAd2.getStore());
//        }
//        if (nativeAd2.getStarRating() == null || nativeAd2.getStarRating().doubleValue() < 3.0d) {
//            nativeAdView.getStarRatingView().setVisibility(4);
//        } else {
//            ((RatingBar) nativeAdView.getStarRatingView()).setRating(nativeAd2.getStarRating().floatValue());
//            nativeAdView.getStarRatingView().setVisibility(0);
//        }
//        if (nativeAd2.getAdvertiser() == null) {
//            nativeAdView.getAdvertiserView().setVisibility(4);
//        } else {
//            ((TextView) nativeAdView.getAdvertiserView()).setText(nativeAd2.getAdvertiser());
//            nativeAdView.getAdvertiserView().setVisibility(0);
//        }
//        nativeAdView.setNativeAd(nativeAd2);
//    }

//    private void nativead() {
//        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.native_advanced));
//        builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//
//            @Override
//            public void onNativeAdLoaded(NativeAd nativeAd) {
//                if (Intruder_Alert.this.nativeAd != null) {
//                    Intruder_Alert.this.nativeAd.destroy();
//                }
//                Intruder_Alert.this.nativeAd = nativeAd;
//                FrameLayout frameLayout = (FrameLayout) Intruder_Alert.this.findViewById(R.id.fl_adplaceholderMain);
//                NativeAdView nativeAdView = (NativeAdView) Intruder_Alert.this.getLayoutInflater().inflate(R.layout.mediation_native_ad, (ViewGroup) null);
//                Intruder_Alert.this.populateNativeAdView(nativeAd, nativeAdView);
//                frameLayout.removeAllViews();
//                frameLayout.addView(nativeAdView);
//                Intruder_Alert.this.NativeShimmer.setVisibility(8);
//                Intruder_Alert.this.NativeShimmer.stopShimmer();
//                frameLayout.setVisibility(0);
//            }
//        }).build();
//        builder.withNativeAdOptions(new NativeAdOptions.Builder().build());
//        builder.withAdListener(new AdListener() {
//
//            public void onAdFailedToLoad(int i) {
//            }
//        }).build().loadAd(new AdRequest.Builder().build());
//    }
}
