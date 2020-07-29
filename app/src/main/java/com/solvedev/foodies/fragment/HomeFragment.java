package com.solvedev.foodies.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.solvedev.foodies.R;

import com.solvedev.foodies.activity.DetailProdukActivity;
import com.solvedev.foodies.activity.ProdukStatusActivity;
import com.solvedev.foodies.activity.SearchActivity;
import com.solvedev.foodies.adapter.ProdukBerandaAdapter;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {


    // widget
    private Toolbar toolbar;
    private EditText edtSearch;
    private SliderLayout slideshow;
    private TextView tvJajanan, tvMinuman, tvAyam, tvCepatSaji;
    private CardView cvTerdekat, cvTerfavorite, cvPromosi, cv24Jam, cvMurah, cvTerlaris;

    private RecyclerView mRvProdukBeranda, mRvProdukPromosi;
    private ProdukBerandaAdapter adapterBeranda, adapterPromosi;
    private List<Food> listProdukKategori, listProdukStatus;

//    private UserPreference preference;
    private ProgressDialog progressDialog;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        slideshow = v.findViewById(R.id.slider);

        HashMap<String,String> file_maps = new HashMap<String, String>();
        file_maps.put("Discount","http://192.168.43.63/foodies_api/image/banner1.jpg");
        file_maps.put("Travel Umroh","http://192.168.43.63/foodies_api/image/banner2.jpg");

        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            slideshow.addSlider(textSliderView);
        }

        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        edtSearch = view.findViewById(R.id.edt_search);

        mRvProdukBeranda = view.findViewById(R.id.rv_produk_beranda);
        mRvProdukPromosi = view.findViewById(R.id.rv_produk_promosi);

        tvJajanan = view.findViewById(R.id.tv_jajanan);
        tvAyam = view.findViewById(R.id.tv_ayam);
        tvMinuman = view.findViewById(R.id.tv_minuman);
        tvCepatSaji = view.findViewById(R.id.tv_cepat_saji);

        cvTerdekat = view.findViewById(R.id.cv_terdekat);
        cvTerfavorite = view.findViewById(R.id.cv_terfavorit);
        cvPromosi = view.findViewById(R.id.cv_promosi);
        cv24Jam = view.findViewById(R.id.cv_24_jam);
        cvMurah = view.findViewById(R.id.cv_murah);
        cvTerlaris = view.findViewById(R.id.cv_terlaris);

        progressDialog = new ProgressDialog(getActivity());


        tvAyam.setOnClickListener(this);
        tvJajanan.setOnClickListener(this);
        tvMinuman.setOnClickListener(this);
        tvCepatSaji.setOnClickListener(this);
        edtSearch.setOnClickListener(this);
        cvTerdekat.setOnClickListener(this);
        cvTerfavorite.setOnClickListener(this);
        cvPromosi.setOnClickListener(this);
        cv24Jam.setOnClickListener(this);
        cvMurah.setOnClickListener(this);
        cvTerlaris.setOnClickListener(this);

        getFoodCategory("Jajanan");
        getFoodStatus("Promosi");

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_notification :

                break;
            case R.id.menu_chat :

                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ayam:
                getFoodCategory("Ayam dan Bebek");
                break;

            case R.id.tv_jajanan:
                getFoodCategory("Jajanan");
                break;

            case R.id.tv_minuman:
                getFoodCategory("Minuman");
                break;

            case R.id.tv_cepat_saji:
                getFoodCategory("Cepat Saji");
                break;

            case R.id.edt_search:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.cv_terdekat:
                Intent intentTerdekat = new Intent(getActivity(), ProdukStatusActivity.class);
                intentTerdekat.putExtra("status","Terdekat");
                startActivity(intentTerdekat);
                break;

            case R.id.cv_terfavorit:
                Intent intentTerfavorite = new Intent(getActivity(), ProdukStatusActivity.class);
                intentTerfavorite.putExtra("status","Terfavorite");
                startActivity(intentTerfavorite);
                break;

            case R.id.cv_promosi:
                Intent intentPromosi = new Intent(getActivity(), ProdukStatusActivity.class);
                intentPromosi.putExtra("status","Promosi");
                startActivity(intentPromosi);
                break;


            case R.id.cv_24_jam:
                Intent intent24Jam = new Intent(getActivity(), ProdukStatusActivity.class);
                intent24Jam.putExtra("status","24Jam");
                startActivity(intent24Jam);
                break;

            case R.id.cv_murah:
                Intent intentMurah = new Intent(getActivity(), ProdukStatusActivity.class);
                intentMurah.putExtra("status","Murah");
                startActivity(intentMurah);
                break;

            case R.id.cv_terlaris:
                Intent intentTerlaris = new Intent(getActivity(), ProdukStatusActivity.class);
                intentTerlaris.putExtra("status","Terlaris");
                startActivity(intentTerlaris);
                break;

        }
    }

    private void getFoodCategory(String Category) {

        progressDialog.setMessage("Harap Tunggu");
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getFoodCategory(Category);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {


                listProdukKategori = response.body().getResult();

                if(listProdukKategori.size() > 0){
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


    private void getFoodStatus(String status) {
        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getFoodStatus(status);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {

                listProdukStatus = response.body().getResult();

                if(listProdukStatus.size() > 0){
                    setAdapter();

                }else {
                    Toast.makeText(getActivity(), "Tidak Ada Data!", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(getActivity(), "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setAdapter() {
        mRvProdukBeranda.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRvProdukBeranda.setHasFixedSize(true);
        adapterBeranda = new ProdukBerandaAdapter(getActivity(), listProdukKategori, new ProdukBerandaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Food model = listProdukKategori.get(position);

                String id =  model.getId_penjual();

                Intent intent = new Intent(getActivity(), DetailProdukActivity.class);
                intent.putExtra("id_penjual",id);
                startActivity(intent);

            }
        });
        mRvProdukBeranda.setAdapter(adapterBeranda);
        adapterBeranda.notifyDataSetChanged();

        mRvProdukPromosi.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRvProdukPromosi.setHasFixedSize(true);
        adapterPromosi = new ProdukBerandaAdapter(getActivity(), listProdukStatus, new ProdukBerandaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Food model = listProdukStatus.get(position);

                String id =  model.getId_penjual();

                Intent intent = new Intent(getActivity(), DetailProdukActivity.class);
                intent.putExtra("id_penjual",id);
                startActivity(intent);

            }
        });
        mRvProdukPromosi.setAdapter(adapterPromosi);
        adapterPromosi.notifyDataSetChanged();

    }
}
