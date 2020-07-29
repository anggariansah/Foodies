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
import com.solvedev.foodies.activity.DetailPesananActivity;
import com.solvedev.foodies.adapter.ProdukAdapter;
import com.solvedev.foodies.adapter.TransactionAdapter;
import com.solvedev.foodies.model.Food;
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
public class TransactionSellerFragment extends Fragment {


    private RecyclerView mRvTransaction;
    private TransactionAdapter adapterTransaction;
    private List<Food> listProdukTransaction;

    private ProgressDialog progressDialog;

    private UserPreferences preference;


    public TransactionSellerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_seller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvTransaction = view.findViewById(R.id.rv_transaksi);

        progressDialog = new ProgressDialog(getActivity());
        preference = new UserPreferences(getActivity());

        getFoodTransaction(preference.getUserId());

    }

    private void getFoodTransaction(String idpenjual) {

        progressDialog.setMessage("Harap Tunggu");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getTransaksiSeller(idpenjual);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {

                listProdukTransaction = response.body().getResult();

                if(listProdukTransaction.size() > 0){
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
        mRvTransaction.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRvTransaction.setHasFixedSize(true);
        adapterTransaction = new TransactionAdapter(getActivity(), listProdukTransaction, new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {



            }
        });
        mRvTransaction.setAdapter(adapterTransaction);
        adapterTransaction.notifyDataSetChanged();
    }

}
