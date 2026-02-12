package com.example.project88trans.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.model.user;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin, btnGoogle;
    private TextView tvRegister, tvForgotPassword;
    private BottomNavigationView bottomNavigation;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            goToMainActivity(true);
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.etEmail);
        edtPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            boolean login = sharedPreferences.getBoolean("isLoggedIn", false);

            if (id == R.id.nav_home) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_order) {
                if (login) startActivity(new Intent(LoginActivity.this, PesananSayaActivity.class));
                else goToLogin();
                overridePendingTransition(0,0);
                return true;
            } else if (id == R.id.nav_armada) {
                if (login) startActivity(new Intent(LoginActivity.this, Adapter.class));
                else goToLogin();
                overridePendingTransition(0,0);
                return true;
            } else if (id == R.id.nav_profile) {
                if (login) goToProfile();
                else goToLogin();
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });

        // Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        // Login manual
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Email dan Password wajib diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ApiResponse<user>> call = apiService.login(email, password);

            call.enqueue(new Callback<ApiResponse<user>>() {
                @Override
                public void onResponse(Call<ApiResponse<user>> call, Response<ApiResponse<user>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse<user> res = response.body();

                        if (res.isSuccess()) {
                            user user = res.getData();

                            // Simpan sesi login
                            saveLoginSession(user);

                            Toast.makeText(LoginActivity.this,
                                    "Selamat datang, " + user.getFirstName(),
                                    Toast.LENGTH_SHORT).show();

                            goToMainActivity(true);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<user>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    // Hanya simpan email untuk Google Sign-In
                    saveLoginSession(account.getEmail());
                    goToMainActivity(true);
                    Toast.makeText(this, "Login Google sukses: " + account.getEmail(), Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Login Google gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveLoginSession(user user) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", user.getFirstName());
        editor.putString("email", user.getEmail());
        editor.putInt("user_id", user.getId()); // ✅ user_id int
        editor.apply();
    }

    private void saveLoginSession(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("email", email);
        editor.apply();
    }

    private void goToMainActivity(boolean fromLogin) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openProfile", fromLogin);
        startActivity(intent);
        finish();
    }

    private void goToProfile() {
        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
    }

    private void goToLogin() {
        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
    }
}
