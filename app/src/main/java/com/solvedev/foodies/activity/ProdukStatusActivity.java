package com.solvedev.foodies.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.solvedev.foodies.R;
import com.solvedev.foodies.adapter.ProdukAdapter;
import com.solvedev.foodies.adapter.ProdukBerandaAdapter;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;
import com.solvedev.foodies.utils.UserPreferences;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdukStatusActivity extends AppCompatActivity {

    private RecyclerView  mRvProdukStatus;
    private ProdukAdapter  adapterStatus;
    private List<Food>  listProdukStatus;

    private String status;

    //    private UserPreference preference;
    private ProgressDialog progressDialog;

    private UserPreferences preference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_status);

        final Bundle extras = getIntent().getExtras();

        if(extras != null){
            status = getIntent().getExtras().getString("status","");
        }

        mRvProdukStatus = findViewById(R.id.rv_produk_status);

        progressDialog = new ProgressDialog(ProdukStatusActivity.this);
        preference = new UserPreferences(ProdukStatusActivity.this);

        getFoodStatus(status);
    }

    private void getFoodStatus(String status) {

        progressDialog.setMessage("Harap Tunggu");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getFoodStatus(status);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {


                listProdukStatus = response.body().getResult();

                if(listProdukStatus.size() > 0){
                    setAdapter();

                }else {
                    Toast.makeText(ProdukStatusActivity.this, "Tidak Ada Data!", Toast.LENGTH_SHORT).show();
                    listProdukStatus.clear();
                }


                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProdukStatusActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setAdapter() {

        mRvProdukStatus.setLayoutManager(new LinearLayoutManager(ProdukStatusActivity.this));
        mRvProdukStatus.setHasFixedSize(true);
        adapterStatus = new ProdukAdapter(ProdukStatusActivity.this, listProdukStatus, new ProdukAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Food model = listProdukStatus.get(position);

                String id =  model.getId_penjual();

                Intent intent = new Intent(ProdukStatusActivity.this, DetailProdukActivity.class);
                intent.putExtra("id_penjual",id);
                startActivity(intent);

            }
        });
        mRvProdukStatus.setAdapter(adapterStatus);
        adapterStatus.notifyDataSetChanged();

    }
}
