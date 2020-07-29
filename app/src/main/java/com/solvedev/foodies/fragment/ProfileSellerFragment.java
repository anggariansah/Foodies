package com.solvedev.foodies.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.solvedev.foodies.R;
import com.solvedev.foodies.activity.DetailProduKSellerActivity;
import com.solvedev.foodies.activity.DetailProdukActivity;
import com.solvedev.foodies.activity.SearchActivity;
import com.solvedev.foodies.activity.TambahProdukActivity;
import com.solvedev.foodies.activity.UpdateProfileActivity;
import com.solvedev.foodies.adapter.ProdukAdapter;
import com.solvedev.foodies.adapter.ProdukBerandaAdapter;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.model.Users;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;
import com.solvedev.foodies.utils.UserPreferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSellerFragment extends Fragment {

    private RecyclerView mRvSeller;
    private ProdukBerandaAdapter adapterSeller;
    private List<Food> listProdukSeller;

    private ProgressDialog progressDialog;

    private UserPreferences preference;
    private List<Users> listProfileUsers;

    private Button btnTambah;
    private ImageView ivNodata;

    public ProfileSellerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_seller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvSeller = view.findViewById(R.id.rv_produk_seller);
        btnTambah = view.findViewById(R.id.btn_tambah);

        ivNodata = view.findViewById(R.id.iv_nodata);

        progressDialog = new ProgressDialog(getActivity());
        preference = new UserPreferences(getActivity());


        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfile(preference.getUserId());
            }
        });


        getFoodSeller(preference.getUserId());

    }


    public void getProfile(String iduser) {
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

                    String nama_resto = model.getNama_resto();
                    String alamat = model.getAlamat();
                    String latitude = model.getLat();
                    String longitude = model.getLng();

                    if(nama_resto == null || alamat == null || latitude == null || longitude == null){
                        new AlertDialog.Builder(getActivity())
                                .setMessage("Harap Update Nama Resto dan Alamat Anda!!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Whatever...
                                    }
                                }).show();
                    }else{
                        Intent intent = new Intent(getActivity(), TambahProdukActivity.class);
                        startActivity(intent);
                    }

                } else {
                    Toast.makeText(getActivity(), "Tidak Ada Data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFoodSeller(String iduser) {

        progressDialog.setMessage("Harap Tunggu");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getFoodSeller(iduser);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {

                listProdukSeller = response.body().getResult();

                if(listProdukSeller.size() > 0){
                    setAdapter();

                }else {
                    Toast.makeText(getActivity(), "Tidak Ada Data!", Toast.LENGTH_SHORT).show();
                    ivNodata.setVisibility(View.VISIBLE);
                    mRvSeller.setVisibility(View.GONE);
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
        mRvSeller.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRvSeller.setHasFixedSize(true);
        adapterSeller = new ProdukBerandaAdapter(getActivity(), listProdukSeller, new ProdukBerandaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Food model = listProdukSeller.get(position);

                String id =  model.getId();

                Intent intent = new Intent(getActivity(), DetailProduKSellerActivity.class);
                intent.putExtra("id_food",id);
                startActivity(intent);
            }
        });
        mRvSeller.setAdapter(adapterSeller);
        adapterSeller.notifyDataSetChanged();
    }

}
