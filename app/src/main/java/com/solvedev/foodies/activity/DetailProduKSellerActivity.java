package com.solvedev.foodies.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.solvedev.foodies.R;
import com.solvedev.foodies.model.Food;
import com.solvedev.foodies.model.ResponsModel;
import com.solvedev.foodies.model.Users;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;
import com.solvedev.foodies.utils.Base;
import com.solvedev.foodies.utils.ImageUtils;
import com.solvedev.foodies.utils.UserPreferences;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailProduKSellerActivity extends AppCompatActivity {

    String iduser, nama, harga, kategori, idfood;
    private EditText edtNama, edtHarga, edtKategori;
    private Button btnEdit, btnDelete;
    private ImageView ivGambar;

    private ProgressDialog progressDialog;

    private static final int GALLERY_REQUEST = 1;
    Bitmap bitmap = null;
    private UserPreferences preference;

    private List<Food> listFood;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produ_kseller);

        edtNama = findViewById(R.id.edt_nama);
        edtHarga = findViewById(R.id.edt_harga);
        edtKategori = findViewById(R.id.edt_kategori);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        ivGambar = findViewById(R.id.iv_gambar);

        progressDialog = new ProgressDialog(DetailProduKSellerActivity.this);
        preference = new UserPreferences(DetailProduKSellerActivity.this);

        final Bundle extras = getIntent().getExtras();

        if(extras != null){
            idfood = getIntent().getExtras().getString("id_food","");
        }

        edtKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(DetailProduKSellerActivity.this)
                        .setTitle("Kategori")
                        .setItems(R.array.kategori, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                edtKategori.setText(getResources().getStringArray(R.array.kategori)[which]);
                            }
                        }).show();
            }
        });

        ivGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailProduKSellerActivity.this)
                        .setItems(R.array.camera, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item

                                String text = getResources().getStringArray(R.array.camera)[which];
                                if(text.equals("Camera")){
                                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(i,0);

                                }else{
                                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                    galleryIntent.setType("image/*");
                                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                                }

                            }
                        })
                        .show();
            }
        });


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iduser = preference.getUserId();
                nama = edtNama.getText().toString();
                harga = edtHarga.getText().toString();
                kategori = edtKategori.getText().toString();

                updateProduk();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduk();
            }
        });

        getDetailFood(idfood);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);

                ivGambar.setImageBitmap(bitmap);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }else if(requestCode == 0){

            bitmap = (Bitmap)data.getExtras().get("data");

            ivGambar.setImageBitmap(bitmap);
        }
    }

    public void getDetailFood(String idfood) {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<Food> getData = api.getDetailFood(idfood);
        getData.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                progressDialog.dismiss();

                listFood = response.body().getResult();

                if (listFood != null) {

                    Food model = listFood.get(0);

                    edtNama.setText(model.getNama());
                    edtHarga.setText(model.getHarga());
                    edtKategori.setText(model.getKategori());

                    Glide
                            .with(DetailProduKSellerActivity.this)
                            .load(Base.url + model.getGambar())
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(ivGambar);



                } else {
                    Toast.makeText(DetailProduKSellerActivity.this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailProduKSellerActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProduk() {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String Gambar = "";

        if(bitmap != null){
            Gambar = ImageUtils.bitmapToBase64String(bitmap, 50);
        }

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.updateProduk(iduser, nama, idfood, harga, kategori, Gambar);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Toast.makeText(DetailProduKSellerActivity.this, "Update Berhasil!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(DetailProduKSellerActivity.this, "Update Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailProduKSellerActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void deleteProduk() {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.deleteProduk(idfood);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Toast.makeText(DetailProduKSellerActivity.this, "Delete Berhasil!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(DetailProduKSellerActivity.this, "Delete Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DetailProduKSellerActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
