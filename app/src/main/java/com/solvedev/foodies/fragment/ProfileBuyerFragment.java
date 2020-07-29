package com.solvedev.foodies.fragment;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.solvedev.foodies.R;
import com.solvedev.foodies.adapter.ProdukAdapter;
import com.solvedev.foodies.adapter.ProdukBerandaAdapter;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileBuyerFragment extends Fragment {

    private RecyclerView mRvBuyer;
    private ProdukBerandaAdapter adapterBuyer;
    private List<Food> listProdukBuyer;

    private ProgressDialog progressDialog;


    public ProfileBuyerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_buyer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvBuyer = view.findViewById(R.id.rv_produk_buyer);

        progressDialog = new ProgressDialog(getActivity());

        getFoodSeller("Jajanan");


    }

    private void getFoodSeller(String iduser) {

        progressDialog.setMessage("Harap Tunggu");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getFoodCategory(iduser);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {

                listProdukBuyer = response.body().getResult();

                if(listProdukBuyer.size() > 0){
                    setAdapter();

                }else {
                    Toast.makeText(getActivity(), "Tidak Ada Data!", Toast.LENGTH_SHORT).show();
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
        mRvBuyer.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRvBuyer.setHasFixedSize(true);
        adapterBuyer = new ProdukBerandaAdapter(getActivity(), listProdukBuyer, new ProdukBerandaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        mRvBuyer.setAdapter(adapterBuyer);
        adapterBuyer.notifyDataSetChanged();
    }

}
