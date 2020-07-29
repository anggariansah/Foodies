package com.solvedev.foodies.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.solvedev.foodies.R;
import com.solvedev.foodies.adapter.ProdukAdapter;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProdukSearchActivity extends AppCompatActivity {

    private RecyclerView mRvProdukSearch;
    private ProdukAdapter adapterSearch;
    private List<Food> listProdukSearch;

    private String search;

    //    private UserPreference preference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_search);

        final Bundle extras = getIntent().getExtras();

        if(extras != null){
            search = getIntent().getExtras().getString("search","");
        }

        mRvProdukSearch = findViewById(R.id.rv_produk_status);

        progressDialog = new ProgressDialog(ProdukSearchActivity.this);


        getFoodStatus("Promosi");
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


                listProdukSearch = response.body().getResult();

                if(listProdukSearch.size() > 0){
                    setAdapter();

                }else {
                    Toast.makeText(ProdukSearchActivity.this, "Tidak Ada Data!", Toast.LENGTH_SHORT).show();
                    listProdukSearch.clear();
                }


                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProdukSearchActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setAdapter() {

        mRvProdukSearch.setLayoutManager(new LinearLayoutManager(ProdukSearchActivity.this));
        mRvProdukSearch.setHasFixedSize(true);
        adapterSearch = new ProdukAdapter(ProdukSearchActivity.this, listProdukSearch, new ProdukAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        mRvProdukSearch.setAdapter(adapterSearch);
        adapterSearch.notifyDataSetChanged();

    }
}
