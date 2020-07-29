package com.solvedev.foodies.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.solvedev.foodies.R;
import com.solvedev.foodies.adapter.KeranjangAdapter;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.model.ResponsModel;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;
import com.solvedev.foodies.utils.UserPreferences;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTransaksiSellerActivity extends AppCompatActivity {

    private GoogleMap mMapG;
    Marker currentMarker;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng startLat;

    private List<Food> listKeranjang;
    private RecyclerView mRvKeranjang;
    private KeranjangAdapter adapterKeranjang;

    private ProgressDialog progressDialog;
    private UserPreferences preference;


    double latitude, longitude;

    TextView tvAlamat, tvTotalHarga;
    private Button btnPesan;
    RadioButton rbCod, rbDiantar;

    String alamat, idpembeli, pengiriman;

    int totalHarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi_seller);

        tvAlamat = findViewById(R.id.tv_alamat);
        tvTotalHarga = findViewById(R.id.tv_total_harga);
        mRvKeranjang = findViewById(R.id.rv_keranjang);
        btnPesan = findViewById(R.id.btn_pesan);
        rbCod = findViewById(R.id.rb_cod);
        rbDiantar = findViewById(R.id.rb_diantar);

        progressDialog = new ProgressDialog(DetailTransaksiSellerActivity.this);
        preference = new UserPreferences(DetailTransaksiSellerActivity.this);


        final Bundle extras = getIntent().getExtras();

        if(extras != null){
            totalHarga = getIntent().getExtras().getInt("total_harga",0);
            tvTotalHarga.setText("Total Harga : "+totalHarga);
        }

        getFoodTransaction(preference.getUserId());


        statusCheck();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                mMapG = mMap;
                mMapG.setMyLocationEnabled(true);
                fetchLastLocation(mMap);

                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        try {
                            LatLng center = mMap.getCameraPosition().target;

                            latitude = center.latitude;
                            longitude = center.longitude;

                            tvAlamat.setText(getStringAddress(center.latitude, center.longitude));
                        } catch (Exception e) {

                        }
                    }
                });


            }
        });


        btnPesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idpembeli = preference.getUserId();
                alamat = tvAlamat.getText().toString();

                if(rbCod.isSelected() || rbCod.isChecked()){
                    pengiriman = "COD";
                }else{
                    pengiriman = "Diantar";
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailTransaksiSellerActivity.this);
                builder.setMessage("Apakah Pesanan Sudah Benar?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                tambahPesanan();
                                updateStatusCart();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    private void fetchLastLocation(final GoogleMap mMap) {
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    startLat = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

                    CameraPosition googlePlex = CameraPosition.builder()
                            .target(startLat)
                            .zoom(17)
                            .bearing(0)
                            .tilt(45)
                            .build();
                }
            }
        });

    }

    public String getStringAddress(Double lat, Double lng) {
        String address = "";
        String city = "";

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address + " " + city;

    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS anda tidak menyala, apakah ingin menyalakan GPS?")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getFoodTransaction(String iduser) {

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
                    Toast.makeText(DetailTransaksiSellerActivity.this, "Tidak Ada Data!", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailTransaksiSellerActivity.this, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter() {
        mRvKeranjang.setLayoutManager(new LinearLayoutManager(DetailTransaksiSellerActivity.this, LinearLayoutManager.VERTICAL, false));
        mRvKeranjang.setHasFixedSize(true);
        adapterKeranjang = new KeranjangAdapter(DetailTransaksiSellerActivity.this, listKeranjang, new KeranjangAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(DetailTransaksiSellerActivity.this, "Pesanan Anda!", Toast.LENGTH_SHORT).show();
            }
        });

        adapterKeranjang.setPositif(DetailTransaksiSellerActivity.this, listKeranjang, new KeranjangAdapter.OnPositifClickListener() {
            @Override
            public void onPositifClick(View v, int posisition, int total, int jumlah) {
                totalHarga = total;
                tvTotalHarga.setText("Total Harga : "+total);
            }
        });

        adapterKeranjang.setNegatif(DetailTransaksiSellerActivity.this, listKeranjang, new KeranjangAdapter.OnNegatifClickListener() {
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

                    Toast.makeText(DetailTransaksiSellerActivity.this, "Delete Keranjang Berhasil!", Toast.LENGTH_SHORT).show();

                    getFoodTransaction(preference.getUserId());

                } else {
                    Toast.makeText(DetailTransaksiSellerActivity.this, "Delete Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailTransaksiSellerActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void tambahPesanan() {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.tambahPesanan(idpembeli, totalHarga, alamat, pengiriman, latitude, longitude);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Toast.makeText(DetailTransaksiSellerActivity.this, "Pesanan Berhasil!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(DetailTransaksiSellerActivity.this, "Tambah Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailTransaksiSellerActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateStatusCart() {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.updateStatusCart(idpembeli);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Toast.makeText(DetailTransaksiSellerActivity.this, "Update Status Berhasil!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(DetailTransaksiSellerActivity.this, "Update Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailTransaksiSellerActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
