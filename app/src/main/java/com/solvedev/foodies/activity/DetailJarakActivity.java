package com.solvedev.foodies.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.solvedev.foodies.R;
import com.solvedev.foodies.utils.DirectionParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DetailJarakActivity extends AppCompatActivity{

    private final static int MY_PERMISSIONS_REQUEST = 32;

    private GoogleMap mMapG;
    private Button btnPilihLokasi;
    Marker currentMarker;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng startLat;

    TextView tvAlamat, tvNama;
    private Double latitude, longitude;

    String nama, alamat;
    double latResto, lngResto;

    TextView tvJarak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jarak);

        tvJarak = findViewById(R.id.tv_jarak);
        tvNama = findViewById(R.id.tv_nama);
        tvAlamat = findViewById(R.id.tv_alamat);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        final Bundle extras = getIntent().getExtras();

        if(extras != null){
            nama = getIntent().getExtras().getString("nama","");
            alamat = getIntent().getExtras().getString("alamat","");
            latResto = getIntent().getExtras().getDouble("lat",0);
            lngResto = getIntent().getExtras().getDouble("lng",0);

            tvNama.setText(nama);
            tvAlamat.setText(alamat);
        }

        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                mMapG = mMap;
                mMapG.setMyLocationEnabled(true);
                fetchLastLocation(mMap);

            }
        });



    }



    private void distance(double lat1, double long1, double lat2, double long2){
        double longDiff = long1 - long2;

        double distance = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(longDiff));
        distance = Math.acos(distance);

        distance = rad2deg(distance);

        distance = distance * 60 * 1.1515;

        distance = distance * 1.609344;

        tvJarak.setText(String.format(Locale.US, "%2f Kilometers", distance));

    }

    private double rad2deg(double distance){
        return (distance * 180.0 / Math.PI);
    }

    private double deg2rad(double lat1){
        return (lat1*Math.PI / 180.0);
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

                    Toast.makeText(DetailJarakActivity.this, "Lat :" + currentLocation.getLatitude() + " Long :" + currentLocation.getLongitude()
                            , Toast.LENGTH_SHORT).show();

                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();

                    distance(latResto,lngResto,latitude, longitude);


                    MarkerOptions mp = new MarkerOptions();

                    mp.position(new LatLng(latitude, longitude));

                    mp.title("my position");

                    mMap.addMarker(mp);

                    MarkerOptions mpResto = new MarkerOptions();

                    mpResto.position(new LatLng(latResto, lngResto));

                    mpResto.title(nama);

                    mMap.addMarker(mpResto);

                }
            }
        });

    }



}
