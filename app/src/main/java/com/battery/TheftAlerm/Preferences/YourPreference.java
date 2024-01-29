package com.battery.TheftAlerm.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class YourPreference {

    private static YourPreference yourPreference;
    private Context context;
    private SharedPreferences sharedPreferences;

    public static YourPreference getInstance(Context context2) {
        if (yourPreference == null) {
            yourPreference = new YourPreference(context2);
        }
        return yourPreference;
    }

    private YourPreference(Context context2) {
        this.sharedPreferences = context2.getSharedPreferences("YourCustomNamedPreference", 0);
        this.context = context2;
    }

    public void setFirstTimePIN(String str, boolean z) {
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.putBoolean(str, z);
        edit.apply();
    }

    public boolean getFirstTimePIN(String str) {
        SharedPreferences sharedPreferences2 = this.sharedPreferences;
        if (sharedPreferences2 != null) {
            return sharedPreferences2.getBoolean(str, false);
        }
        return false;
    }

    public void set_sw(String str, boolean z) {
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    public boolean get_sw(String str) {
        SharedPreferences sharedPreferences2 = this.sharedPreferences;
        if (sharedPreferences2 != null) {
            return sharedPreferences2.getBoolean(str, false);
        }
        return false;
    }

    public void set_swintruder(String str, boolean z) {
        SharedPreferences.Editor edit = this.sharedPreferences.edit();
        edit.putBoolean(str, z);
        edit.commit();
    }

    public boolean get_swintruder(String str) {
        SharedPreferences sharedPreferences2 = this.sharedPreferences;
        if (sharedPreferences2 != null) {
            return sharedPreferences2.getBoolean(str, false);
        }
        return false;
    }


}
