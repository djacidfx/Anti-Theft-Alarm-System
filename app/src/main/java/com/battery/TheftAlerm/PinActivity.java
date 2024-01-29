package com.battery.TheftAlerm;

import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.biometric.BiometricPrompt;
import com.amirarcane.lockscreen.andrognito.pinlockview.IndicatorDots;
import com.amirarcane.lockscreen.andrognito.pinlockview.PinLockListener;
import com.amirarcane.lockscreen.andrognito.pinlockview.PinLockView;
import com.amirarcane.lockscreen.util.Utils;
import com.battery.TheftAlerm.Preferences.YourPreference;
import com.battery.TheftAlerm.intruder.MagicService;
import com.battery.TheftAlerm.utils.Constants_AntiTheft;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class PinActivity extends AppCompatActivity {
    public static final String EXTRA_FONT_NUM = "numFont";
    public static final String EXTRA_FONT_TEXT = "textFont";
    public static final String EXTRA_SET_PIN = "set_pin";
    private static final String FINGER_PRINT_KEY = "FingerPrintKey";
    private static final String KEY_PIN = "pin";
    private static final int PIN_LENGTH = 4;
    private static final String PREFERENCES = "com.amirarcane.lockscreen";
    public static final int RESULT_BACK_PRESSED = 1;
    public static final String TAG = "EnterPinActivity";
    private AnimatedVectorDrawable fingerprintToCross;
    private AnimatedVectorDrawable fingerprintToTick;
    private Cipher mCipher;
    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintManager mFingerprintManager;
    private String mFirstPin = "";
    private IndicatorDots mIndicatorDots;
    private KeyGenerator mKeyGenerator;
    private KeyStore mKeyStore;
    private KeyguardManager mKeyguardManager;
    private PinLockView mPinLockView;
    private boolean mSetPin = false;
    SharedPreferences mSharedPreferences;
    private TextView mTextAttempts;
    private TextView mTextTitle;
    YourPreference preference;
    private AnimatedVectorDrawable showFingerprint;

    public static Intent getIntent(Context context, boolean z) {
        Intent intent = new Intent(context, PinActivity.class);
        intent.putExtra("set_pin", z);
        return intent;
    }

    public static Intent getIntent(Context context, String str, String str2) {
        Intent intent = new Intent(context, PinActivity.class);
        intent.putExtra("textFont", str);
        intent.putExtra("numFont", str2);
        return intent;
    }

    public static Intent getIntent(Context context, boolean z, String str, String str2) {
        Intent intent = getIntent(context, str, str2);
        intent.putExtra("set_pin", z);
        return intent;
    }

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_pin);
        this.preference = YourPreference.getInstance(this);
        this.mTextAttempts = (TextView) findViewById(R.id.attempts);
        this.mTextTitle = (TextView) findViewById(R.id.title);
        this.mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        boolean booleanExtra = getIntent().getBooleanExtra("set_pin", false);
        this.mSetPin = booleanExtra;
        if (booleanExtra) {
            changeLayoutForSetPin();
        } else if (getPinFromSharedPreferences().equals("")) {
            changeLayoutForSetPin();
            this.mSetPin = true;
        } else {
//            new BiometricPrompt(this, Executors.newSingleThreadExecutor(), new BiometricPrompt.AuthenticationCallback() {
 //
//                @Override // androidx.biometric.BiometricPrompt.AuthenticationCallback
//                public void onAuthenticationError(int i, CharSequence charSequence) {
//                    super.onAuthenticationError(i, charSequence);
//                }
//
//                @Override // androidx.biometric.BiometricPrompt.AuthenticationCallback
//                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
//                    super.onAuthenticationSucceeded(authenticationResult);
//                    PinActivity.this.setResult(-1);
//                    PinActivity.this.finish();
//                }
//
//                @Override // androidx.biometric.BiometricPrompt.AuthenticationCallback
//                public void onAuthenticationFailed() {
//                    super.onAuthenticationFailed();
//                }
//            }).authenticate(new BiometricPrompt.PromptInfo.Builder().setTitle("Dont Touch My phone").setNegativeButtonText("USE APP PASSWORD").build());
        }
        PinLockListener r0 = new PinLockListener() {

            @Override
            public void onComplete(String str) {
                if (PinActivity.this.mSetPin) {
                    PinActivity.this.setPin(str);
                } else {
                    PinActivity.this.checkPin(str);
                }
            }

            @Override // com.amirarcane.lockscreen.andrognito.pinlockview.PinLockListener
            public void onEmpty() {
                Log.d("EnterPinActivity", "Pin empty");
            }

            @Override // com.amirarcane.lockscreen.andrognito.pinlockview.PinLockListener
            public void onPinChange(int i, String str) {
                Log.d("EnterPinActivity", "Pin changed, new length " + i + " with intermediate pin " + str);
            }
        };
        this.mPinLockView = (PinLockView) findViewById(R.id.pinlockView);
        IndicatorDots indicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        this.mIndicatorDots = indicatorDots;
        this.mPinLockView.attachIndicatorDots(indicatorDots);
        this.mPinLockView.setPinLockListener(r0);
        this.mPinLockView.setPinLength(4);
        this.mIndicatorDots.setIndicatorType(2);
        checkForFont();
    }

    private void checkForFont() {
        Intent intent = getIntent();
        if (intent.hasExtra("textFont")) {
            setTextFont(intent.getStringExtra("textFont"));
        }
        if (intent.hasExtra("numFont")) {
            setNumFont(intent.getStringExtra("numFont"));
        }
    }

    private void setTextFont(String str) {
        try {
            Typeface createFromAsset = Typeface.createFromAsset(getAssets(), str);
            this.mTextTitle.setTypeface(createFromAsset);
            this.mTextAttempts.setTypeface(createFromAsset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setNumFont(String str) {
        try {
            this.mPinLockView.setTypeFace(Typeface.createFromAsset(getAssets(), str));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateKey() throws FingerprintException {
        try {
            this.mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            this.mKeyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore");
            this.mKeyStore.load(null);
            this.mKeyGenerator.generateKey();
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | CertificateException e) {
            throw new FingerprintException(e);
        }
    }

    public boolean initCipher() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                this.mCipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            }
            try {
                this.mKeyStore.load(null);
                this.mCipher.init(1, (SecretKey) this.mKeyStore.getKey(FINGER_PRINT_KEY, null));
                return true;
            } catch (Exception unused) {
                Log.e("EnterPinActivity", "Failed to init Cipher");
                return false;
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException unused2) {
            Log.e("EnterPinActivity", "Failed to get Cipher");
            return false;
        }
    }

    private void writePinToSharedPreferences(String str) {
        getSharedPreferences("com.amirarcane.lockscreen", 0).edit().putString(KEY_PIN, Utils.sha256(str)).apply();
    }

    private String getPinFromSharedPreferences() {
        return getSharedPreferences("com.amirarcane.lockscreen", 0).getString(KEY_PIN, "");
    }


    private void setPin(String str) {
        if (this.mFirstPin.equals("")) {
            this.mFirstPin = str;
            this.mTextTitle.setText(getString(R.string.pinlock_secondPin));
            this.mPinLockView.resetPinLockView();
        } else if (str.equals(this.mFirstPin)) {
            writePinToSharedPreferences(str);
            setResult(-1);
            finish();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            shake();
            this.mTextTitle.setText(getString(R.string.pinlock_tryagain));
            this.mPinLockView.resetPinLockView();
            this.mFirstPin = "";
        }
    }

    private void onOffSwitch(boolean z) {
        this.preference.set_sw(Constants_AntiTheft.PIN_SWITCH, z);
        Toast.makeText(this, z ? "PIN Activated" : "PIN Deactivating", 0).show();
    }


    private void checkPin(String str) {
        if (Utils.sha256(str).equals(getPinFromSharedPreferences())) {
            setResult(-1);
            finish();
            return;
        }
        shake();
        if (isintruder()) {
            startService(new Intent(this, MagicService.class));
        }
        this.mTextAttempts.setText(getString(R.string.pinlock_wrongpin));
        this.mPinLockView.resetPinLockView();
    }

    public boolean isintruder() {
        return this.preference.get_swintruder(Constants_AntiTheft.INTRUDER_SWITCH);
    }

    private void shake() {
        new ObjectAnimator();
        ObjectAnimator.ofFloat(this.mPinLockView, "translationX", 0.0f, 25.0f, -25.0f, 25.0f, -25.0f, 15.0f, -15.0f, 6.0f, -6.0f, 0.0f).setDuration(1000L).start();
    }

    private void changeLayoutForSetPin() {
        this.mTextAttempts.setVisibility(8);
        this.mTextTitle.setText(getString(R.string.pinlock_settitle));
    }

    @Override // androidx.activity.ComponentActivity
    public void onBackPressed() {
        setResult(1);
        super.onBackPressed();
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception exc) {
            super(exc);
        }
    }
}
