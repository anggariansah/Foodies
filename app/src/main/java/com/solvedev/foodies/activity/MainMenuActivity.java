package com.solvedev.foodies.activity;

import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.solvedev.foodies.R;
import com.solvedev.foodies.adapter.ProdukAdapter;
import com.solvedev.foodies.fragment.CartFragment;
import com.solvedev.foodies.fragment.FavoriteFragment;
import com.solvedev.foodies.fragment.HomeFragment;
import com.solvedev.foodies.fragment.ProfileFragment;
import com.solvedev.foodies.fragment.TransactionFragment;
import com.solvedev.foodies.model.Koordinat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


public class MainMenuActivity extends AppCompatActivity {

    private BottomNavigationView navigationView;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    LatLng startLat;

    private GoogleMap mMapG;

    public static double latitude, longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        navigationView = findViewById(R.id.menu_bottom);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainMenuActivity.this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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


        HomeFragment home = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content, home);
        transaction.commit();

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.nav_home :
                    HomeFragment home = new HomeFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content, home);
                    transaction.commit();

                    return true;

                case R.id.nav_favorite :
                    FavoriteFragment favoriteFragment = new FavoriteFragment();
                    FragmentTransaction transactionFavorite = getSupportFragmentManager().beginTransaction();
                    transactionFavorite.replace(R.id.content, favoriteFragment);
                    transactionFavorite.commit();

                    return true;

                case R.id.nav_cart :
                    CartFragment cartFragment = new CartFragment();
                    FragmentTransaction transactionCart = getSupportFragmentManager().beginTransaction();
                    transactionCart.replace(R.id.content, cartFragment);
                    transactionCart.commit();

                    return true;

                case R.id.nav_transaction :
                    TransactionFragment transactionFragment = new TransactionFragment();
                    FragmentTransaction transactionTrans = getSupportFragmentManager().beginTransaction();
                    transactionTrans.replace(R.id.content, transactionFragment);
                    transactionTrans.commit();

                    return true;

                case R.id.nav_profile :
                    ProfileFragment profileFragment = new ProfileFragment();
                    FragmentTransaction transactionProfile = getSupportFragmentManager().beginTransaction();
                    transactionProfile.replace(R.id.content, profileFragment);
                    transactionProfile.commit();

                    return true;

            }

            return false;
        }
    };

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



                    Toast.makeText(MainMenuActivity.this, "Lat :" + currentLocation.getLatitude() + " Long :" + currentLocation.getLongitude()
                            , Toast.LENGTH_SHORT).show();

                     latitude = currentLocation.getLatitude();
                     longitude = currentLocation.getLongitude();

                   ProdukAdapter model = new ProdukAdapter(latitude, longitude);

                }
            }
        });

    }

}
