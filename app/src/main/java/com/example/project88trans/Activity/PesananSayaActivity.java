package com.example.project88trans.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project88trans.Adapater.PesananAdapter;
import com.example.project88trans.Api.ApiClient;
import com.example.project88trans.Api.ApiResponse;
import com.example.project88trans.Api.ApiService;
import com.example.project88trans.Fragment.RiwayatPesananFragment;
import com.example.project88trans.model.BusRental;
import com.example.project88trans.model.TourRental;
import com.example.project88trans.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import android.widget.FrameLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesananSayaActivity extends AppCompatActivity {

    private RecyclerView rvPesanan;

    private PesananAdapter adapter;
    private List<Object> listPesanan;
    private ApiService apiService;
    private ChipGroup chipGroupFilter;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan_saya);

        // ===== Inisialisasi view =====
        rvPesanan = findViewById(R.id.rvPesanan);
        chipGroupFilter = findViewById(R.id.chipGroupFilter);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        FrameLayout btnRiwayat = findViewById(R.id.btnRiwayat);
        btnRiwayat.setOnClickListener(v -> {
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





        rvPesanan.setLayoutManager(new LinearLayoutManager(this));
        listPesanan = new ArrayList<>();
        adapter = new PesananAdapter(this, listPesanan);
        rvPesanan.setAdapter(adapter);


        apiService = ApiClient.getClient().create(ApiService.class);

        SharedPreferences sp = getSharedPreferences("UserSession", MODE_PRIVATE);
        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
        int userId = sp.getInt("user_id", 0);

        if (!isLoggedIn || userId == 0) {
            Toast.makeText(this, "Harap login dahulu!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        adapter.setOnRentalActionListener(new PesananAdapter.OnRentalActionListener() {
            @Override
            public void onPaymentClick(Object rental) {
                Intent intent = new Intent(PesananSayaActivity.this, PembayaranActivity.class);

                if (rental instanceof BusRental) {
                    BusRental b = (BusRental) rental;
                    intent.putExtra("rental_type", "bus");
                    intent.putExtra("rental_id", b.getId());
                    intent.putExtra("total", b.getTotalPrice());
                } else if (rental instanceof TourRental) {
                    TourRental t = (TourRental) rental;
                    intent.putExtra("rental_type", "tour");
                    intent.putExtra("rental_id", t.getId());
                    intent.putExtra("total", t.getTotalPrice());
                }

                startActivity(intent);
            }

            @Override
            public void onDetailClick(Object rental) {
                // Tidak digunakan di activity ini
            }
        });


        loadPesanan(userId);

        // ===== Chip Filter =====
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipSemua) {
                adapter.filterCategory("all");
            } else if (checkedId == R.id.chipBus) {
                adapter.filterCategory("bus");
            } else if (checkedId == R.id.chipTour) {
                adapter.filterCategory("tour");
            }
        });

        // ===== BottomNavigation =====
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_order) {
                return true;
            } else if (id == R.id.nav_armada) {
                startActivity(new Intent(this, ArmadaActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
        bottomNavigation.setSelectedItemId(R.id.nav_order);
    }

    private void loadPesanan(int userId) {
        listPesanan.clear();
        adapter.notifyDataSetChanged();

        // ===== Load Bus Rentals =====
        apiService.getBusRentalsByUser(userId).enqueue(new Callback<ApiResponse<List<BusRental>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BusRental>>> call, Response<ApiResponse<List<BusRental>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BusRental> buses = response.body().getData();
                    if (buses != null) {
                        for (BusRental b : buses) {
                            String status = b.getStatus() != null ? b.getStatus().trim().toLowerCase() : "";
                            // Masukkan hanya pesanan aktif (pending / wait_confirmation / confirmed)
                            if (!status.equals("completed") && !status.equals("canceled")) {
                                listPesanan.add(b);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BusRental>>> call, Throwable t) {
                Toast.makeText(PesananSayaActivity.this, "Gagal load bus rentals", Toast.LENGTH_SHORT).show();
                Log.e("PesananSaya", "Bus rental load failed", t);
            }
        });

        // ===== Load Tour Rentals =====
        apiService.getTourRentalsByUser(userId).enqueue(new Callback<ApiResponse<List<TourRental>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TourRental>>> call, Response<ApiResponse<List<TourRental>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TourRental> tours = response.body().getData();
                    if (tours != null) {
                        for (TourRental t : tours) {
                            String status = t.getStatus() != null ? t.getStatus().trim().toLowerCase() : "";
                            if (!status.equals("completed") && !status.equals("canceled")) {
                                listPesanan.add(t);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TourRental>>> call, Throwable t) {
                Toast.makeText(PesananSayaActivity.this, "Gagal load tour rentals", Toast.LENGTH_SHORT).show();
                Log.e("PesananSaya", "Tour rental load failed", t);
            }
        });
    }
}
