package com.battery.TheftAlerm.intruder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.battery.TheftAlerm.R;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectionsActivity extends AppCompatActivity {
    public static final String URI_KEY = "URI";
    CollectionsAdapter adapter;
    List<Bitmap> bitmapList;
    String[] filesinfolder;
    RecyclerView recyclerView;
    ArrayList<String> uriList;

     @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_collections);
        this.bitmapList = new ArrayList();
        this.uriList = new ArrayList<>();
        getImagesList();
        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCollections);
        this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        this.recyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_itemmargin)));
        CollectionsAdapter collectionsAdapter = new CollectionsAdapter(this.uriList);
        this.adapter = collectionsAdapter;
        this.recyclerView.setAdapter(collectionsAdapter);
    }

    private void getImagesList() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Anti-Theft Alarm System");
        file.mkdirs();
        if (file.listFiles() != null) {
            this.filesinfolder = GetFiles(file + "/");
        }
        String[] strArr = this.filesinfolder;
        if (strArr != null) {
            this.uriList.addAll(Arrays.asList(strArr));
        }
    }

    public String[] GetFiles(String str) {
        File file = new File(str);
        file.mkdirs();
        File[] listFiles = file.listFiles();
        String[] strArr = new String[listFiles.length];
        if (listFiles.length == 0) {
            return null;
        }
        for (int i = 0; i < listFiles.length; i++) {
            strArr[i] = str + listFiles[i].getName();
        }
        return strArr;
    }

     @Override
    public void onResume() {
        super.onResume();
        getImagesList();
        this.adapter.notifyDataSetChanged();
    }
}
