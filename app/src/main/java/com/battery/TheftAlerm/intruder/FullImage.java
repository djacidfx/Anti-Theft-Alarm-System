package com.battery.TheftAlerm.intruder;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import com.battery.TheftAlerm.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;

public class FullImage extends AppCompatActivity {
    Animation FabClose;
    Animation FabOpen;
    Animation FabRAntiClockWise;
    Animation FabRClockWise;
    FloatingActionButton fabDelete;
    FloatingActionButton fabPlus;
    FloatingActionButton fabShare;
    boolean isOpen = false;
    CoordinatorLayout parentView;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_full_image);
        final String stringExtra = getIntent().getStringExtra("single_photo_path");
        ImageView imageView = (ImageView) findViewById(R.id.singleImageView);
        this.FabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        this.FabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        this.FabRClockWise = AnimationUtils.loadAnimation(this, R.anim.rotate_clockwise);
        this.FabRAntiClockWise = AnimationUtils.loadAnimation(this, R.anim.rotate_anticlockwise);
        this.parentView = (CoordinatorLayout) findViewById(R.id.parentView);
        File file = new File(stringExtra);
        if (file.exists()) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.shareFab);
        this.fabShare = floatingActionButton;
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                FullImage.this.shareImage(stringExtra);
            }
        });
        FloatingActionButton floatingActionButton2 = (FloatingActionButton) findViewById(R.id.deleteFab);
        this.fabDelete = floatingActionButton2;
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                FullImage.this.delete_image(stringExtra);
            }
        });
        FloatingActionButton floatingActionButton3 = (FloatingActionButton) findViewById(R.id.plusFab);
        this.fabPlus = floatingActionButton3;
        floatingActionButton3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (FullImage.this.isOpen) {
                    FullImage.this.fabDelete.startAnimation(FullImage.this.FabClose);
                    FullImage.this.fabShare.startAnimation(FullImage.this.FabClose);
                    FullImage.this.fabPlus.startAnimation(FullImage.this.FabRAntiClockWise);
                    FullImage.this.fabShare.setClickable(false);
                    FullImage.this.fabDelete.setClickable(false);
                    FullImage.this.isOpen = false;
                    return;
                }
                FullImage.this.fabDelete.startAnimation(FullImage.this.FabOpen);
                FullImage.this.fabShare.startAnimation(FullImage.this.FabOpen);
                FullImage.this.fabPlus.startAnimation(FullImage.this.FabRClockWise);
                FullImage.this.fabShare.setClickable(true);
                FullImage.this.fabDelete.setClickable(true);
                FullImage.this.isOpen = true;
            }
        });
    }


    private void shareImage(String str) {
        Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), "com.battery.antitheftalarm.fileprovider", new File(str));
        startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(this).setStream(uriForFile).setType("text/html").getIntent().setAction("android.intent.action.SEND").setDataAndType(uriForFile, "image/*").addFlags(1), "Share Image"));
    }

    public void delete_image(String str) {
        File file = new File(str);
        if (file.exists() && file.delete()) {
            sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(str))));
            Intent intent = new Intent(this, CollectionsActivity.class);
            intent.setFlags(67108864);
            startActivity(intent);
            finish();
        }
    }
}
