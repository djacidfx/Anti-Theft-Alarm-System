package com.battery.TheftAlerm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaButtonReceiver extends BroadcastReceiver {
    int volumePrev = 0;

    public boolean onKeyDown(int i) {
        return true;
    }

    public void onReceive(Context context, Intent intent) {
        if ("android.media.VOLUME_CHANGED_ACTION".equals(intent.getAction())) {
            int intExtra = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
            onKeyDown(24);
            if (this.volumePrev < intExtra) {
                onKeyDown(24);
            } else {
                onKeyDown(25);
            }
        }
    }
}
