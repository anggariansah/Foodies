package com.solvedev.foodies.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.solvedev.foodies.R;
import com.solvedev.foodies.activity.MainMenuActivity;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.model.Koordinat;
import com.solvedev.foodies.utils.Base;

import java.util.List;
import java.util.Locale;

public class ProdukAdapter extends RecyclerView.Adapter<ProdukAdapter.HolderData> {

    private List<Food> mList;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    HolderData mHolder;

    double latResto, lngResto, latMe, lngMe;


    public ProdukAdapter(Context ctx, List<Food> mList, OnItemClickListener mOnItemClickListener) {
        this.mList = mList;
        this.ctx = ctx;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public ProdukAdapter(double latMe, double lngMe){
        this.latMe = latMe;
        this.lngMe = lngMe;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produk, parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(HolderData holder, final int position) {
        Food model = mList.get(position);

        mHolder = holder;

        String id = model.getId();
        holder.tvNama.setText(model.getNama());
        holder.tvAlamat.setText(model.getAlamat());
        holder.tvHarga.setText(model.getHarga());

        latResto = Double.parseDouble(model.getLat());
        lngResto = Double.parseDouble(model.getLng());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(v, position);
            }
        });


        Glide
                .with(ctx)
                .load(Base.url + model.getGambar())
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.gambar);

        Koordinat koordinat = new Koordinat();

        distance(MainMenuActivity.latitude, MainMenuActivity.longitude, latResto, lngResto);

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class HolderData extends RecyclerView.ViewHolder {

        TextView tvNama, tvHarga, tvAlamat, tvJarak;
        ImageView gambar;

        public View container;

        public HolderData(View v) {
            super(v);

            container = v;

            tvNama = v.findViewById(R.id.tv_nama);
            tvAlamat = v.findViewById(R.id.tv_alamat);
            tvJarak = v.findViewById(R.id.tv_jarak);
            tvHarga = v.findViewById(R.id.tv_harga);
            gambar = v.findViewById(R.id.iv_gambar);

        }

    }

    public interface OnItemClickListener {
        void onItemClick(View v, int posisition);
    }


    private void distance(double lat1, double long1, double lat2, double long2) {
        double longDiff = long1 - long2;

        double distance = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(longDiff));
        distance = Math.acos(distance);

        distance = rad2deg(distance);

        distance = distance * 60 * 1.1515;

        distance = distance * 1.609344;

        mHolder.tvJarak.setText(String.format(Locale.US, "%2f Kilometers", distance));

    }

    private double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    private double deg2rad(double lat1) {
        return (lat1 * Math.PI / 180.0);
    }



}
