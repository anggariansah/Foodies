package com.solvedev.foodies.activity;

import android.app.Activity;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.solvedev.foodies.R;
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

public class UpdateProfileActivity extends AppCompatActivity {

    private Button btnPlacePicker, btnUpdate;
    private static final int PLACE_PICKER_REQUEST = 15;
    private String iduser, latitude, longitude, alamat, email , nama, notelp , nama_resto;

    private EditText edtNama, edtEmail, edtNotelp, edtNamaResto, edtLatitude, edtLongitude, edtAlamat;
    private ImageView ivBanner;

    private ProgressDialog progressDialog;

    private UserPreferences preference;
    private List<Users> listProfileUsers;

    private static final int GALLERY_REQUEST = 1;
    Bitmap bitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        edtNama = findViewById(R.id.edt_nama);
        edtEmail = findViewById(R.id.edt_email);
        edtNotelp = findViewById(R.id.edt_notelp);
        edtNamaResto = findViewById(R.id.edt_nama_resto);
        edtLongitude = findViewById(R.id.edt_lng);
        edtLatitude = findViewById(R.id.edt_lat);
        edtAlamat = findViewById(R.id.edt_alamat);
        ivBanner = findViewById(R.id.iv_banner);

        progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        preference = new UserPreferences(UpdateProfileActivity.this);

        btnPlacePicker = findViewById(R.id.btn_place_picker);
        btnUpdate = findViewById(R.id.btn_update_profile);

        btnPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(UpdateProfileActivity.this, PlaceUpdateActivity.class);
                startActivityForResult(a, PLACE_PICKER_REQUEST);
            }
        });

        ivBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UpdateProfileActivity.this)
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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iduser = preference.getUserId();
                email = edtEmail.getText().toString().trim();
                nama = edtNama.getText().toString().trim();
                notelp = edtNotelp.getText().toString().trim();
                nama_resto = edtNamaResto.getText().toString().trim();
                alamat = edtAlamat.getText().toString().trim();
                latitude = edtLatitude.getText().toString().trim();
                longitude = edtLongitude.getText().toString().trim();

                updateProfile();

            }
        });

        getProfile(preference.getUserId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);

                ivBanner.setImageBitmap(bitmap);

            } catch (IOException e) {

                e.printStackTrace();
            }
        }else if(requestCode == 0){

            bitmap = (Bitmap)data.getExtras().get("data");

            ivBanner.setImageBitmap(bitmap);
        }

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            latitude = data.getStringExtra("latitude");
            longitude = data.getStringExtra("longitude");
            alamat = data.getStringExtra("alamat");


            edtLatitude.setText(latitude);
            edtLongitude.setText(longitude);
            edtAlamat.setText(alamat);


            Toast.makeText(this, "Latitude : " + latitude + "\n" + "Longitude : " + longitude, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String Gambar = "";

        if(bitmap != null){
            Gambar = ImageUtils.bitmapToBase64String(bitmap, 50);
        }

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.updateProfile(iduser, nama, email, notelp, nama_resto, alamat, latitude, longitude, Gambar);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Toast.makeText(UpdateProfileActivity.this, "Update Data Berhasil!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Update Data failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UpdateProfileActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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

                    edtNama.setText(model.getNama());
                    edtEmail.setText(model.getEmail());
                    edtNamaResto.setText(model.getNama_resto());
                    edtNotelp.setText(model.getNo_hp());
                    edtLatitude.setText(model.getLat());
                    edtLongitude.setText(model.getLng());
                    edtAlamat.setText(model.getAlamat());

                    Glide
                            .with(UpdateProfileActivity.this)
                            .load(Base.url + model.getBanner())
                            .centerCrop()
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(ivBanner);

                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Tidak Ada Data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UpdateProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
