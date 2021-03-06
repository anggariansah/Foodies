package com.solvedev.foodies.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.solvedev.foodies.R;
import com.solvedev.foodies.model.ResponsModel;
import com.solvedev.foodies.network.ApiRequest;
import com.solvedev.foodies.network.RetrofitServer;
import com.solvedev.foodies.utils.UserPreferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 1;

    private Button btnRegister;
    private EditText edtNama, edtPassword, edtEmail, edtNotelp;
    private TextView tvLogin;

    private final int REQUEST_CODE = 101;

    String nama, email, password;

    private ProgressDialog progressDialog;

    private UserPreferences preference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btn_register);
        edtNama = findViewById(R.id.edt_nama);
        edtPassword = findViewById(R.id.edt_password);
        edtEmail = findViewById(R.id.edt_email);
        edtNotelp = findViewById(R.id.edt_notelp);
        tvLogin = findViewById(R.id.tv_login);

        progressDialog = new ProgressDialog(RegisterActivity.this);
        preference = new UserPreferences(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginAccountActivity.class);
                startActivity(intent);
            }
        });

        //GOOGLE
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            nama = account.getDisplayName();
            email = account.getEmail();
            password = account.getId();

            registerAccount(nama, email, password, "");

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("LOGIN", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    private void verify() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String nama = edtNama.getText().toString().trim();
        String notelp = edtNotelp.getText().toString().trim();

        if (email.equals("") && password.equals("") && nama.equals("") && notelp.equals("")) {
            Toast.makeText(this, "Mohon Lengkapi !", Toast.LENGTH_SHORT).show();
        } else {
            registerAccount(nama, email, password, notelp);
        }
    }

    private void registerAccount(String nama, String email, String password, String notelp) {
        progressDialog.setMessage("Harap Tunggu ..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRequest api = RetrofitServer.getClient().create(ApiRequest.class);
        Call<ResponsModel> login = api.registerAccount(nama, email, password, notelp);
        login.enqueue(new Callback<ResponsModel>() {
            @Override
            public void onResponse(Call<ResponsModel> call, Response<ResponsModel> response) {
                progressDialog.dismiss();

                String kode = response.body().getKode();

                if (kode.equals("1")) {

                    Intent intent = new Intent(RegisterActivity.this, LoginAccountActivity.class);
                    startActivity(intent);

                    Toast.makeText(RegisterActivity.this, "Register Berhasil!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(RegisterActivity.this, "Register failed!!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponsModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Network Error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
