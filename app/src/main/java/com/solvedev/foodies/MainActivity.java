package com.solvedev.foodies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.solvedev.foodies.activity.LoginActivity;
import com.solvedev.foodies.activity.MainMenuActivity;
import com.solvedev.foodies.utils.UserPreferences;

public class MainActivity extends AppCompatActivity {

    private UserPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preference = new UserPreferences(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSession();
            }
        }, 3000);


    }

    private void checkSession () {

        if (preference.isUserLoggedIn()) {
            startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

    }
}
