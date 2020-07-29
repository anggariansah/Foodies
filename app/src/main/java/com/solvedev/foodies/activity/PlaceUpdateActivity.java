package com.solvedev.foodies.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.solvedev.foodies.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class PlaceUpdateActivity extends FragmentActivity {

    private GoogleMap mMapG;
    private Button btnPilihLokasi;
    Marker currentMarker;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng startLat;

    TextView tvAlamat;
    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_update);
        statusCheck();

        tvAlamat = findViewById(R.id.tv_alamat);
        btnPilihLokasi =  findViewById(R.id.btn_pilih_lokasi);

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

                            Toast.makeText(PlaceUpdateActivity.this, "Lat :" + center.latitude + " Long :" + center.longitude
                                    , Toast.LENGTH_SHORT).show();

                            latitude = center.latitude;
                            longitude = center.longitude;

                            tvAlamat.setText(getStringAddress(center.latitude, center.longitude));
                        } catch (Exception e) {

                        }
                    }
                });


            }
        });

        btnPilihLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = getStringAddress(latitude, longitude);
                String lat = String.valueOf(latitude);
                String lon = String.valueOf(longitude);
                setResult(Activity.RESULT_OK, new Intent().putExtra("latitude", lat)
                        .putExtra("longitude", lon)
                        .putExtra("alamat", address));
                finish();
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

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 1000, null);

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
}
