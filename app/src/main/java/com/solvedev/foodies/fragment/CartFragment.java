package com.solvedev.foodies.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.solvedev.foodies.R;
import com.solvedev.foodies.activity.DetailPesananActivity;
import com.solvedev.foodies.activity.DetailProduKSellerActivity;
import com.solvedev.foodies.activity.DetailProdukActivity;
import com.solvedev.foodies.activity.ProdukStatusActivity;
import com.solvedev.foodies.adapter.KeranjangAdapter;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.model.ResponsModel;
import com.solvedev.foodies.model.Users;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;
import com.solvedev.foodies.utils.Base;
import com.solvedev.foodies.utils.UserPreferences;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {


    private RecyclerView mRvKeranjang;
    private KeranjangAdapter adapterKeranjang;
    private List<Food> listKeranjang;
    private List<Users> listResto;
    private ProgressDialog progressDialog;

    private ArrayList<Integer> arrayTotal = new ArrayList<>();

    private UserPreferences preference;

    Button btnPesan;
    ScrollView svLayout;
    ImageView ivNodata;

    TextView tvTotalHarga, tvNama, tvAlamat;

    int total;
    int jumlah = 1;
    int harga;
    int totalHarga = 0;


    public CartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvKeranjang = view.findViewById(R.id.rv_keranjang);

        tvTotalHarga = view.findViewById(R.id.tv_total_harga);
        tvNama = view.findViewById(R.id.tv_nama);
        tvAlamat = view.findViewById(R.id.tv_alamat);
        btnPesan = view.findViewById(R.id.btn_pesan);
        svLayout = view.findViewById(R.id.sv_layout);
        ivNodata = view.findViewById(R.id.iv_nodata);

        progressDialog = new ProgressDialog(getActivity());
        preference = new UserPreferences(getActivity());


        getFoodCart(preference.getUserId());
        getDetailRestoCart(preference.getUserId());

        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailPesananActivity.class);
                intent.putExtra("total_harga",totalHarga);
                startActivity(intent);
            }
        });

    }

    public void getDetailRestoCart(String iduser) {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Users> getData = api.getDetailRestoCart(iduser);
        getData.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                progressDialog.dismiss();

                listResto = response.body().getResult();

                if (listResto != null) {

                    Users model = listResto.get(0);

                    tvNama.setText(model.getNama());
                    tvAlamat.setText(model.getAlamat());
                    tvTotalHarga.setText("Total Harga : "+model.getTotal());

                    totalHarga = model.getTotal() == null ? 0 : Integer.parseInt(model.getTotal());


                } else {
                    Toast.makeText(getActivity(), "Tidak Ada Data!", Toast.LENGTH_SHORT).show();

                    ivNodata.setVisibility(View.VISIBLE);
                    svLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFoodCart(String iduser) {

        progressDialog.setMessage("Harap Tunggu");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getFoodCart(iduser);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {

                listKeranjang = response.body().getResult();

                if(listKeranjang.size() > 0){

                    setAdapter();

                }else {
                    Toast.makeText(getActivity(), "Tidak Ada Data!", Toast.LENGTH_SHORT).show();
                    ivNodata.setVisibility(View.VISIBLE);
                    svLayout.setVisibility(View.GONE);
                }

                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        mRvKeranjang.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRvKeranjang.setHasFixedSize(true);
        adapterKeranjang = new KeranjangAdapter(getActivity(), listKeranjang, new KeranjangAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "Pesanan Anda!", Toast.LENGTH_SHORT).show();
            }
        });

        adapterKeranjang.setPositif(getActivity(), listKeranjang, new KeranjangAdapter.OnPositifClickListener() {
            @Override
            public void onPositifClick(View v, int posisition, int total, int jumlah) {
                totalHarga = total;
                tvTotalHarga.setText("Total Harga : "+total);
            }
        });

        adapterKeranjang.setNegatif(getActivity(), listKeranjang, new KeranjangAdapter.OnNegatifClickListener() {
            @Override
            public void onNegatifClick(View v, int posisition, int total, int jumlah) {
                totalHarga = total;
                tvTotalHarga.setText("Total Harga : "+total);

                Food model = listKeranjang.get(posisition);

                if(jumlah <= 0){
                    deleteCart(model.getId());
                }
            }
        });


        mRvKeranjang.setAdapter(adapterKeranjang);
        adapterKeranjang.notifyDataSetChanged();
    }


    private void deleteCart(String idcart) {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.deleteCart(idcart);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Toast.makeText(getActivity(), "Delete Keranjang Berhasil!", Toast.LENGTH_SHORT).show();

                    getFoodCart(preference.getUserId());

                } else {
                    Toast.makeText(getActivity(), "Delete Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
