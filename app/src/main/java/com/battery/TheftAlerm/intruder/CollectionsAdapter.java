package com.battery.TheftAlerm.intruder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.battery.TheftAlerm.R;
import java.util.ArrayList;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.CollectionsHolder> {
    Context _context;
    ArrayList<String> arrayList;

    public CollectionsAdapter(ArrayList<String> arrayList2) {
        ArrayList<String> arrayList3 = new ArrayList<>();
        this.arrayList = arrayList3;
        arrayList3.addAll(arrayList2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public CollectionsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        this._context = viewGroup.getContext();
        return new CollectionsHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycler_view_collection, viewGroup, false));
    }

    public void onBindViewHolder(CollectionsHolder collectionsHolder, final int i) {
        Glide.with(this._context).load(this.arrayList.get(i)).into(collectionsHolder.imageView);
        collectionsHolder.imageView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                CollectionsAdapter.this._context.startActivity(new Intent(CollectionsAdapter.this._context, FullImage.class).putExtra("single_photo_path", CollectionsAdapter.this.arrayList.get(i)));
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.arrayList.size();
    }

    /* access modifiers changed from: package-private */
    public class CollectionsHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public CollectionsHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.imageView);
        }
    }
}
