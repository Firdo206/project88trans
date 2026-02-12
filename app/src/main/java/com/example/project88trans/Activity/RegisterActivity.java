package com.example.project88trans.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.R;
import com.example.project88trans.model.user;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone, etEmail, etPassword, etConfirmPassword;
    private Button btnJoin, btnGoogleRegister;
    private TextView tvGoToLogin;

    private GoogleSignInClient mGoogleSignInClient;
    private ApiService apiService;

    private static final int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi view
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnJoin = findViewById(R.id.btnJoin);
        btnGoogleRegister = findViewById(R.id.btnGoogleRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Inisialisasi API Service
        apiService = ApiClient.getClient().create(ApiService.class);

        // Konfigurasi Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Tombol register manual
        btnJoin.setOnClickListener(v -> registerManual());

        // Tombol register pakai Google
        btnGoogleRegister.setOnClickListener(v -> googleRegister());

        // Pindah ke halaman login
        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerManual() {
        String first = etFirstName.getText().toString().trim();
        String last = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(first) || TextUtils.isEmpty(last) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) ||
                TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Panggil API register.php
        Call<ApiResponse<user>> call = apiService.register(first, last, email, phone, pass);
        call.enqueue(new Callback<ApiResponse<user>>() {
            @Override
            public void onResponse(Call<ApiResponse<user>> call, Response<ApiResponse<user>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<user> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(RegisterActivity.this, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Gagal register manual!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<user>> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void googleRegister() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String name = account.getDisplayName();
                    String email = account.getEmail();

                    // Panggil google_login.php di server
                    Call<ApiResponse<user>> call = apiService.googleLogin(name, email);
                    call.enqueue(new Callback<ApiResponse<user>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<user>> call, Response<ApiResponse<user>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                ApiResponse<user> apiResponse = response.body();
                                if (apiResponse.isSuccess()) {
                                    Toast.makeText(RegisterActivity.this, "Login dengan Google berhasil!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Gagal login Google!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<user>> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
