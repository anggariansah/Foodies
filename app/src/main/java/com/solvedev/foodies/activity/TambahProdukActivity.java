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

import com.solvedev.foodies.R;
import com.solvedev.foodies.model.ResponsModel;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;
import com.solvedev.foodies.utils.ImageUtils;
import com.solvedev.foodies.utils.UserPreferences;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahProdukActivity extends AppCompatActivity {

    String iduser, nama, harga, kategori;
    private EditText edtNama, edtHarga, edtKategori;
    private Button btnTamnbah;
    private ImageView ivGambar;

    private ProgressDialog progressDialog;

    private static final int GALLERY_REQUEST = 1;
    Bitmap bitmap = null;
    private UserPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        edtNama = findViewById(R.id.edt_nama);
        edtHarga = findViewById(R.id.edt_harga);
        edtKategori = findViewById(R.id.edt_kategori);
        btnTamnbah = findViewById(R.id.btn_tambah);
        ivGambar = findViewById(R.id.iv_gambar);

        progressDialog = new ProgressDialog(TambahProdukActivity.this);
        preference = new UserPreferences(TambahProdukActivity.this);

        edtKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(TambahProdukActivity.this)
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
                new AlertDialog.Builder(TambahProdukActivity.this)
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


        btnTamnbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iduser = preference.getUserId();
                nama = edtNama.getText().toString();
                harga = edtHarga.getText().toString();
                kategori = edtKategori.getText().toString();

                tambahProduk();
            }
        });
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

    private void tambahProduk() {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String Gambar = "";

        if(bitmap != null){
            Gambar = ImageUtils.bitmapToBase64String(bitmap, 50);
        }

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.tambahProduk(iduser, nama, harga, kategori, Gambar);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Toast.makeText(TambahProdukActivity.this, "Tambah Data Berhasil!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(TambahProdukActivity.this, "Tambah Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(TambahProdukActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
