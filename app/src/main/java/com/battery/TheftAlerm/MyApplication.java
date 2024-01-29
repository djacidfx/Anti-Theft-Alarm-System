package com.battery.TheftAlerm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import java.util.Date;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String TAG = "MyApplication";
    private AppOpenAdManager appOpenAdManager;
    private Activity currentActivity;

    public interface OnShowAdCompleteListener {
        void onShowAdComplete();
    }

    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void onActivityStopped(Activity activity) {
    }

    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {

            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        this.appOpenAdManager = new AppOpenAdManager();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        this.appOpenAdManager.showAdIfAvailable(this.currentActivity);
    }

    public void onActivityStarted(Activity activity) {
        if (!this.appOpenAdManager.isShowingAd) {
            this.currentActivity = activity;
        }
    }

    public void showAdIfAvailable(Activity activity, OnShowAdCompleteListener onShowAdCompleteListener) {
        this.appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
    }

    /* access modifiers changed from: private */
    public class AppOpenAdManager {
        private static final String LOG_TAG = "AppOpenAdManager";
        private AppOpenAd appOpenAd = null;
        private boolean isLoadingAd = false;
        private boolean isShowingAd = false;
        private long loadTime = 0;

        public AppOpenAdManager() {
        }


        private void loadAd(Context context) {
            if (!this.isLoadingAd && !isAdAvailable()) {
                this.isLoadingAd = true;
                AppOpenAd.load(context, MyApplication.this.getResources().getString(R.string.app_open), new AdManagerAdRequest.Builder().build(), 1, (AppOpenAd.AppOpenAdLoadCallback) new AppOpenAd.AppOpenAdLoadCallback() {

                    public void onAdLoaded(AppOpenAd appOpenAd) {
                        AppOpenAdManager.this.appOpenAd = appOpenAd;
                        AppOpenAdManager.this.isLoadingAd = false;
                        AppOpenAdManager.this.loadTime = new Date().getTime();
                    }

                    @Override // com.google.android.gms.ads.AdLoadCallback
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        AppOpenAdManager.this.isLoadingAd = false;
                    }
                });
            }
        }

        private boolean wasLoadTimeLessThanNHoursAgo(long j) {
            return new Date().getTime() - this.loadTime < j * 3600000;
        }

        private boolean isAdAvailable() {
            return this.appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
        }


        private void showAdIfAvailable(Activity activity) {
            showAdIfAvailable(activity, new OnShowAdCompleteListener() {

                @Override
                public void onShowAdComplete() {
                }
            });
        }


        private void showAdIfAvailable(final Activity activity, final OnShowAdCompleteListener onShowAdCompleteListener) {
            if (!this.isShowingAd) {
                if (!isAdAvailable()) {
                    Log.d(LOG_TAG, "The app open ad is not ready yet.");
                    onShowAdCompleteListener.onShowAdComplete();
                    loadAd(activity);
                    return;
                }
                Log.d(LOG_TAG, "Will show ad.");
                this.appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                    @Override
                    public void onAdShowedFullScreenContent() {
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        AppOpenAdManager.this.appOpenAd = null;
                        AppOpenAdManager.this.isShowingAd = false;
                        onShowAdCompleteListener.onShowAdComplete();
                        AppOpenAdManager.this.loadAd(activity);
                    }

                    @Override // com.google.android.gms.ads.FullScreenContentCallback
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AppOpenAdManager.this.appOpenAd = null;
                        AppOpenAdManager.this.isShowingAd = false;
                        onShowAdCompleteListener.onShowAdComplete();
                        AppOpenAdManager.this.loadAd(activity);
                    }
                });
                this.isShowingAd = true;
                this.appOpenAd.show(activity);
            }
        }
    }
}
