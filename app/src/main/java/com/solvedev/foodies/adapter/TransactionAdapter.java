package com.solvedev.foodies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.solvedev.foodies.R;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.utils.Base;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.HolderData> {

    private List<Food> mList;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;


    public TransactionAdapter(Context ctx, List<Food> mList, OnItemClickListener mOnItemClickListener) {
        this.mList = mList;
        this.ctx = ctx;
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @Override
    public HolderData onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction,parent, false);
        HolderData holder = new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(HolderData holder, final int position) {
        Food model = mList.get(position);

        String id = model.getId();
        holder.tvJumlah.setText(model.getJumlah() + " Pesanan");
        holder.tvNama.setText(model.getNama());
        holder.tvHarga.setText(model.getHarga());

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

        TextView tvJumlah, tvNama,tvHarga;
        ImageView gambar;

        public View container;

        public HolderData(View v)
        {
            super(v);

            container = v;

            tvNama = v.findViewById(R.id.tv_nama);
            tvJumlah = v.findViewById(R.id.tv_jumlah);
            tvHarga = v.findViewById(R.id.tv_harga);
            gambar = v.findViewById(R.id.iv_gambar);

        }

    }

    public interface OnItemClickListener{
        void onItemClick(View v, int posisition);
    }
}
