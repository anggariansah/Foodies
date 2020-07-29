package com.solvedev.foodies.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.solvedev.foodies.R;
import com.solvedev.foodies.adapter.DetailProdukAdapter;
import com.solvedev.foodies.adapter.KeranjangAdapter;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.model.ResponsModel;
import com.solvedev.foodies.model.Users;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;
import com.solvedev.foodies.utils.Base;
import com.solvedev.foodies.utils.ImageUtils;
import com.solvedev.foodies.utils.UserPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProdukActivity extends AppCompatActivity {

    private ImageView ivPlace, ivBanner;

    private RecyclerView mRvKeranjang;
    private DetailProdukAdapter adapterKeranjang;
    private ProgressDialog progressDialog;

    private ArrayList<Integer> arrayTotal = new ArrayList<>();
    private List<Users> listProfileUsers;
    private List<Food> listKeranjang;

    TextView tvNama, tvAlamat, tvJarak;
    private UserPreferences preference;


    double lat, lng;

    String idpenjual;

    int total;
    int jumlah = 0;
    int harga;
    int totalHarga = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);

        ivPlace = findViewById(R.id.iv_place);
        ivBanner = findViewById(R.id.iv_banner);
        tvNama = findViewById(R.id.tv_nama);
        tvAlamat = findViewById(R.id.tv_alamat);
        tvJarak = findViewById(R.id.tv_jarak);

        progressDialog = new ProgressDialog(DetailProdukActivity.this);
        preference = new UserPreferences(DetailProdukActivity.this);


        mRvKeranjang = findViewById(R.id.rv_keranjang);


        final Bundle extras = getIntent().getExtras();

        if(extras != null){
            idpenjual = getIntent().getExtras().getString("id_penjual","");
        }


        getListDetail(idpenjual);

        ivPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailProdukActivity.this, DetailJarakActivity.class);
                intent.putExtra("nama",tvNama.getText().toString());
                intent.putExtra("alamat",tvAlamat.getText().toString());
                intent.putExtra("lat",lat);
                intent.putExtra("lng",lng);
                startActivity(intent);
            }
        });

        getDetailResto(idpenjual);
    }

    private void getListDetail(String iduser) {

        progressDialog.setMessage("Harap Tunggu");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getFoodSeller(iduser);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {

                listKeranjang = response.body().getResult();

                if(listKeranjang.size() > 0){
                    setAdapter();

                }else {
                    Toast.makeText(DetailProdukActivity.this, "Tidak Ada Data!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailProdukActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getDetailResto(String iduser) {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Users> getData = api.getDetailProfile(iduser);
        getData.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                progressDialog.dismiss();

                listProfileUsers = response.body().getResult();

                if (listProfileUsers != null) {

                    Users model = listProfileUsers.get(0);

                    tvNama.setText(model.getNama_resto());
                    tvAlamat.setText(model.getAlamat());

                    Glide
                            .with(DetailProdukActivity.this)
                            .load(Base.url + model.getBanner())
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(ivBanner);

                    lat = Double.parseDouble(model.getLat());
                    lng = Double.parseDouble(model.getLng());

                    distance(MainMenuActivity.latitude, MainMenuActivity.longitude, lat, lng);


                } else {
                    Toast.makeText(DetailProdukActivity.this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailProdukActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        tvJarak.setText(String.format(Locale.US, "%2f Kilometers", distance));

    }

    private double rad2deg(double distance) {
        return (distance * 180.0 / Math.PI);
    }

    private double deg2rad(double lat1) {
        return (lat1 * Math.PI / 180.0);
    }

    private void setAdapter() {
        mRvKeranjang.setLayoutManager(new LinearLayoutManager(DetailProdukActivity.this, LinearLayoutManager.VERTICAL, false));
        mRvKeranjang.setHasFixedSize(true);
        adapterKeranjang = new DetailProdukAdapter(DetailProdukActivity.this, listKeranjang, new DetailProdukAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(DetailProdukActivity.this, "Testing", Toast.LENGTH_SHORT).show();
            }
        });

        adapterKeranjang.setAdd(DetailProdukActivity.this, listKeranjang, new DetailProdukAdapter.OnAddClickListener() {
            @Override
            public void onAddClick(View v, int posisition) {
                Food model = listKeranjang.get(posisition);

                tambahKeranjang(model.getId(), preference.getUserId());

            }
        });

        mRvKeranjang.setAdapter(adapterKeranjang);
        adapterKeranjang.notifyDataSetChanged();
    }

    private void tambahKeranjang(String idfood, String iduser) {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.tambahKeranjang(idfood, iduser);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Toast.makeText(DetailProdukActivity.this, "Berhasil Tambah Keranjang!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(DetailProdukActivity.this, "Tambah Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailProdukActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
