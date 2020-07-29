package com.solvedev.foodies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.solvedev.foodies.R;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.utils.Base;

import java.util.List;

public class DetailProdukAdapter extends RecyclerView.Adapter<DetailProdukAdapter.HolderData> {

    private List<Food> mList;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private OnAddClickListener mOnAddClickListener;



    public DetailProdukAdapter(Context ctx, List<Food> mList, OnItemClickListener mOnItemClickListener) {
        this.mList = mList;
        this.ctx = ctx;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setAdd(Context ctx, List<Food> mList, OnAddClickListener mOnAddClickListener) {
        this.mList = mList;
        this.ctx = ctx;
        this.mOnAddClickListener = mOnAddClickListener;
    }


    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_data,parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HolderData holder, final int position) {
        final Food model = mList.get(position);

        String id = model.getId();
        holder.tvNama.setText(model.getNama());
        holder.tvHarga.setText(model.getHarga());


        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnAddClickListener.onAddClick(v, position);

                holder.btnAdd.setText("Keranjang");
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
        Button btnAdd;
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
            btnAdd = v.findViewById(R.id.btn_tambah);

        }

    }

    public interface OnItemClickListener{
        void onItemClick(View v, int posisition);
    }

    public interface OnAddClickListener{
        void onAddClick(View v, int posisition);
    }

}
