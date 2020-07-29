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
import android.widget.Toast;

import com.solvedev.foodies.R;
import com.solvedev.foodies.activity.DetailProdukActivity;
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
public class FavoriteFragment extends Fragment {

    private RecyclerView mRvFavorite;
    private ProdukAdapter adapterFavorite;
    private List<Food> listProdukFavorite;

    private ProgressDialog progressDialog;


    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvFavorite = view.findViewById(R.id.rv_produk_favorite);

        progressDialog = new ProgressDialog(getActivity());

        getFoodFavorite("Jajanan");


    }

    private void getFoodFavorite(String Category) {

        progressDialog.setMessage("Harap Tunggu");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getFoodCategory(Category);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {

                listProdukFavorite = response.body().getResult();

                if(listProdukFavorite.size() > 0){
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
        mRvFavorite.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRvFavorite.setHasFixedSize(true);
        adapterFavorite = new ProdukAdapter(getActivity(), listProdukFavorite, new ProdukAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Food model = listProdukFavorite.get(position);

                String id =  model.getId_penjual();

                Intent intent = new Intent(getActivity(), DetailProdukActivity.class);
                intent.putExtra("id_penjual",id);
                startActivity(intent);

            }
        });
        mRvFavorite.setAdapter(adapterFavorite);
        adapterFavorite.notifyDataSetChanged();
    }
}
