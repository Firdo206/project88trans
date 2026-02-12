package com.example.project88trans.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.R;
import com.example.project88trans.model.UserResponse;

import java.util.ArrayList;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {

    TextView tvNama, tvEmail, tvTelepon, tvTanggal, tvAsal, tvBus;
    Switch switchSaya;
    Button btnLanjutkan;
    ImageView btnBackSticky;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        sp = getSharedPreferences("USER", MODE_PRIVATE);

        initViews();
        loadUserData();
        loadPerjalananData();
        setupSwitch();
        setupButton();
        setupStepIndicator();

        // Tombol back di header
        btnBackSticky.setOnClickListener(v -> showExitConfirmation());

        // Handle back gesture / tombol fisik
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitConfirmation();
            }
        });
    }

    private void initViews() {
        tvNama = findViewById(R.id.tvNama);
        tvEmail = findViewById(R.id.tvEmail);
        tvTelepon = findViewById(R.id.tvTelepon);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvAsal = findViewById(R.id.tvAsal);
        switchSaya = findViewById(R.id.switchSaya);
        btnLanjutkan = findViewById(R.id.btnLanjutkan);
        btnBackSticky = findViewById(R.id.btnBackSticky);
        tvBus = findViewById(R.id.tvBus);
    }

    private void loadUserData() {

        // Pakai session yang sama dengan DetailAccountFragment
        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);

        String nama = sp.getString("nama", null);
        String email = sp.getString("email", null);
        String phone = sp.getString("phone", null);

        // Jika datanya sudah ada, tampilkan
        if (nama != null && email != null && phone != null) {
            tvNama.setText(nama);
            tvEmail.setText(email);
            tvTelepon.setText(phone);
        }
        // Jika email ada tapi nama & phone belum ada, ambil dari API
        else if (email != null) {
            loadUserDataFromApi(email);
        }
        else {
            Toast.makeText(this, "User tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserDataFromApi(String email) {

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getUserByEmail(email).enqueue(new Callback<ApiResponse<UserResponse.User>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponse.User>> call, Response<ApiResponse<UserResponse.User>> response) {

                if(response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    UserResponse.User user = response.body().getData();

                    tvNama.setText(user.getNama());
                    tvEmail.setText(user.getEmail());
                    tvTelepon.setText(user.getPhone());

                    // SIMPAN ke session yang SAMA dengan Profile
                    SharedPreferences.Editor editor = getSharedPreferences("UserSession", MODE_PRIVATE).edit();
                    editor.putString("nama", user.getNama());
                    editor.putString("email", user.getEmail());
                    editor.putString("phone", user.getPhone());
                    editor.apply();

                } else {
                    Toast.makeText(BookingActivity.this, "Gagal memuat data user", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponse.User>> call, Throwable t) {
                Toast.makeText(BookingActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPerjalananData() {
        Intent intent = getIntent();
        String jenis = intent.getStringExtra("type");

        if (jenis == null || jenis.isEmpty()) return;

        if (jenis.equals("tour")) {

                int tourId = intent.getIntExtra("package_id", -1);
                String tourNama = intent.getStringExtra("nama_tour");
                ArrayList<String> fasilitas = intent.getStringArrayListExtra("fasilitas");
                ArrayList<String> destinasiUtama = intent.getStringArrayListExtra("destinasi_utama");
                String durasi = intent.getStringExtra("durasi");
                double harga = intent.getDoubleExtra("price", 0);
                int NumberPeople = intent.getIntExtra("fixed_capacity",0);

                tvAsal.setText(tourNama);           // Nama tour
                tvTanggal.setText(durasi);          // Durasi

                if (fasilitas != null && !fasilitas.isEmpty()) {
                    tvBus.setText(fasilitas.get(0)); // atau tampilkan semua sesuai desain
                } else {
                    tvBus.setText("-");
                }


        } else if (jenis.equals("bus")) {

            // Ambil data dari intent
            int busId = intent.getIntExtra("bus_id", -1);
            String busNama   = intent.getStringExtra("bus_name");
            String busType   = intent.getStringExtra("bus_type");
            int kapasitas    = intent.getIntExtra("bus_capacity", 0);
            int harga        = intent.getIntExtra("bus_price", 0);

            // SET DATA DIRI
            tvAsal.setText(busNama);       // Nama bus
            tvTanggal.setText(busType);    // Tipe bus
            tvBus.setText(String.valueOf(kapasitas));// Bus type lagi (sesuai kebutuhanmu)
        }
    }



    private void setupSwitch() {
        switchSaya.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btnLanjutkan.setEnabled(true);
                btnLanjutkan.setBackgroundResource(R.drawable.bg_button_primary);
                btnLanjutkan.setTextColor(getResources().getColor(R.color.white));
            } else {
                btnLanjutkan.setEnabled(false);
                btnLanjutkan.setBackgroundResource(R.drawable.bg_button_disabled);
                btnLanjutkan.setTextColor(getResources().getColor(R.color.gray_disabled_text));
            }
        });
    }


    private void setupButton() {
        btnLanjutkan.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, BookingDetailActivity.class);
            String type = getIntent().getStringExtra("type");
            intent.putExtra("type", type);

            if ("bus".equals(type)) {
                intent.putExtra("bus_id", getIntent().getIntExtra("bus_id", -1));
                intent.putExtra("bus_name", getIntent().getStringExtra("bus_name"));
                intent.putExtra("bus_type", getIntent().getStringExtra("bus_type"));
                intent.putExtra("bus_capacity", getIntent().getIntExtra("bus_capacity", 0));
                intent.putExtra("bus_price", (double) getIntent().getIntExtra("bus_price", 0));
            } else if ("tour".equals(type)) {
                intent.putExtra("package_id", getIntent().getIntExtra("package_id", -1));
                intent.putExtra("nama_tour", getIntent().getStringExtra("nama_tour"));
                intent.putExtra("destinasi_utama", getIntent().getStringArrayListExtra("destinasi_utama"));
                intent.putExtra("price", getIntent().getDoubleExtra("price", 0));
                intent.putExtra("fixed_capacity", getIntent().getIntExtra("fixed_capacity",0));
            }

            startActivity(intent);
        });
    }

    private void setupStepIndicator() {
        TextView step1 = findViewById(R.id.step1);
        TextView step2 = findViewById(R.id.step2);
        TextView step3 = findViewById(R.id.step3);

        TextView labelStep1 = findViewById(R.id.labelStep1);
        TextView labelStep2 = findViewById(R.id.labelStep2);
        TextView labelStep3 = findViewById(R.id.labelStep3);

        // Step 1 aktif (angka + teks merah)
        step1.setBackgroundResource(R.drawable.bg_step_active);
        step1.setTextColor(getResources().getColor(R.color.white));
        labelStep1.setTextColor(getResources().getColor(R.color.primary_red));

        // Step 2 & 3 non aktif
        step2.setBackgroundResource(R.drawable.bg_step_inactive);
        step2.setTextColor(getResources().getColor(R.color.gray_disabled));
        labelStep2.setTextColor(getResources().getColor(R.color.gray_disabled));

        step3.setBackgroundResource(R.drawable.bg_step_inactive);
        step3.setTextColor(getResources().getColor(R.color.gray_disabled));
        labelStep3.setTextColor(getResources().getColor(R.color.gray_disabled));
    }



    private void showExitConfirmation() {

        MaterialAlertDialogBuilder builder =
                new MaterialAlertDialogBuilder(this, R.style.CustomDialog);

        builder.setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya", (dialog, which) -> finish())
                .setNegativeButton("Tidak", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Styling tombol
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                getResources().getColor(R.color.primary_red)
        );

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                getResources().getColor(R.color.black)
        );
    }

}
