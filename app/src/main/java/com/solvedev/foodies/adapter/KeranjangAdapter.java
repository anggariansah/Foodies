package com.solvedev.foodies.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.solvedev.foodies.R;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.utils.Base;

import java.util.List;

public class KeranjangAdapter extends RecyclerView.Adapter<KeranjangAdapter.HolderData> {

    private List<Food> mList;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private OnPositifClickListener mOnPositifClickListener;
    private OnNegatifClickListener mOnNegatifClickListener;

    int total;
    int jumlah = 1;


    public KeranjangAdapter(Context ctx, List<Food> mList, OnItemClickListener mOnItemClickListener) {
        this.mList = mList;
        this.ctx = ctx;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setPositif(Context ctx, List<Food> mList, OnPositifClickListener mOnPositifClickListener) {
        this.mList = mList;
        this.ctx = ctx;
        this.mOnPositifClickListener = mOnPositifClickListener;
    }

    public void setNegatif(Context ctx, List<Food> mList, OnNegatifClickListener mOnNegatifClickListener) {
        this.mList = mList;
        this.ctx = ctx;
        this.mOnNegatifClickListener = mOnNegatifClickListener;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keranjang,parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HolderData holder, final int position) {
        final Food model = mList.get(position);

        String id = model.getId();
        holder.tvNama.setText(model.getNama());
        holder.tvHarga.setText(model.getHarga());

        holder.ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                jumlah = jumlah + 1;

                holder.tvJumlah.setText(String.valueOf(jumlah));

                total = total + (Integer.parseInt(model.getHarga()) * jumlah);

                mOnPositifClickListener.onPositifClick(v, position, total, jumlah);

            }
        });

        holder.ibMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                jumlah = jumlah - 1;

                holder.tvJumlah.setText(String.valueOf(jumlah));

                total = total + (Integer.parseInt(model.getHarga()) * jumlah);

                mOnNegatifClickListener.onNegatifClick(v, position, total, jumlah);

            }
        });

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
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class HolderData extends RecyclerView.ViewHolder {

        TextView tvNama, tvHarga, tvJumlah;
        ImageButton ibPlus, ibMinus;
        ImageView gambar;

        public View container;

        public HolderData(View v)
        {
            super(v);

            container = v;

            tvNama =  v.findViewById(R.id.tv_nama);
            tvHarga = v.findViewById(R.id.tv_harga);
            tvJumlah = v.findViewById(R.id.tv_jumlah);
            gambar = v.findViewById(R.id.iv_gambar);
            ibPlus = v.findViewById(R.id.ib_plus);
            ibMinus = v.findViewById(R.id.ib_minus);

        }

    }

    public interface OnItemClickListener{
        void onItemClick(View v, int posisition);
    }

    public interface OnPositifClickListener{
        void onPositifClick(View v, int posisition, int total, int jumlah);
    }

    public interface OnNegatifClickListener{
        void onNegatifClick(View v, int posisition, int total, int jumlah);
    }
}
