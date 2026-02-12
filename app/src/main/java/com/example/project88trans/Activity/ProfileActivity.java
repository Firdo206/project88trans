package com.example.project88trans.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import com.example.project88trans.Fragment.RiwayatPesananFragment;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Fragment.AccountInfoFragment;
import com.example.project88trans.Fragment.HelpBottomSheetFragment;
import com.example.project88trans.R;
import com.example.project88trans.model.UserResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvNama, tvEmail;
    private ImageView btnBack;
    private LinearLayout akun, riwayat, bantuan, keluar;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvNama = findViewById(R.id.tvNama);
        tvEmail = findViewById(R.id.tvEmail);
        btnBack = findViewById(R.id.btnBack);
        akun = findViewById(R.id.akun);
        riwayat = findViewById(R.id.riwayat);
        bantuan = findViewById(R.id.bantuan);
        keluar = findViewById(R.id.keluar);

        prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String email = prefs.getString("email", null);

        if(email == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // tampil loading sementara
        tvNama.setText("Loading...");
        tvEmail.setText(email);

        fetchUserFromAPI(email);

        btnBack.setOnClickListener(v -> handleBackPress());

        akun.setOnClickListener(v -> {
            if(isFinishing()) return;
            AccountInfoFragment fragment = new AccountInfoFragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter (back)
                            R.anim.slide_out_right  // popExit (back)
                    )
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        });
        riwayat.setOnClickListener(v -> {
            if (isFinishing()) return;

            RiwayatPesananFragment fragment = new RiwayatPesananFragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter (back)
                            R.anim.slide_out_right  // popExit (back)
                    )
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        });

        bantuan.setOnClickListener(v -> {
            if (isFinishing()) return;

            HelpBottomSheetFragment fragment = new HelpBottomSheetFragment();
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // enter
                            R.anim.slide_out_left,  // exit
                            R.anim.slide_in_left,   // popEnter (back)
                            R.anim.slide_out_right  // popExit (back)
                    )
                    .replace(android.R.id.content, fragment)
                    .addToBackStack(null)
                    .commitAllowingStateLoss();
        });

        keluar.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.nav_home){
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if(id == R.id.nav_order){
                startActivity(new Intent(this, PesananSayaActivity.class));
                finish();
                return true;
            } else if(id == R.id.nav_armada){
                startActivity(new Intent(this, ArmadaActivity.class));
                finish();
                return true;
            } else if(id == R.id.nav_profile){
                return true;
            }
            return false;
        });
        bottomNavigation.setSelectedItemId(R.id.nav_profile);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPress();
            }
        });
    }

    private void fetchUserFromAPI(String email){
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getUserByEmail(email).enqueue(new Callback<ApiResponse<UserResponse.User>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse.User>> call, Response<ApiResponse<UserResponse.User>> response) {
                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()){
                    UserResponse.User user = response.body().getData();
                    tvNama.setText(user.getNama());
                    tvEmail.setText(user.getEmail());

                    // update SharedPreferences
                    prefs.edit()
                            .putString("nama", user.getNama())
                            .putString("phone", user.getPhone())
                            .apply();
                } else {
                    tvNama.setText("Pengguna");
                    Toast.makeText(ProfileActivity.this, "Gagal memuat data user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse.User>> call, Throwable t) {
                tvNama.setText("Pengguna");
                Toast.makeText(ProfileActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleBackPress() {
        if (findViewById(R.id.frameRiwayatProfile).getVisibility() == View.VISIBLE) {
            // kembali ke tampilan profile
            findViewById(R.id.frameRiwayatProfile).setVisibility(View.GONE);
            findViewById(R.id.tvNama).getRootView().setVisibility(View.VISIBLE);
            findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }


    public void openEditNamaFragment() {
        com.example.project88trans.Fragment.EditNamaFragment fragment =
                new com.example.project88trans.Fragment.EditNamaFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void openEditPhoneFragment() {
        com.example.project88trans.Fragment.EditPhoneFragment fragment =
                new com.example.project88trans.Fragment.EditPhoneFragment();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                        android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}
